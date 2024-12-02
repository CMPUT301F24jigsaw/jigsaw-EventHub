package com.example.eventhub_jigsaw.entrant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.organizer.event.OrganizerEventPage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserInvitePageActivity extends Fragment {

    private static final String TAG = "UserInvitePageActivity";

    private List<Event> eventList; // List to store events
    private UserInvitePageAdapter adapter;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String userId; // User ID for querying the database

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_invitepage, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        adapter = new UserInvitePageAdapter(eventList, getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        // Fetch user ID (e.g., from shared preferences or settings)
        // Replace this with actual logic to retrieve the current user's ID
        userId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        // Fetch events accepted by the organizer for the user
        fetchAcceptedEvents();

        return view;
    }

    private void fetchAcceptedEvents() {
        // Ensure userId is not null
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch the user's eventAcceptedByOrganizer list
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> eventIds = (List<String>) documentSnapshot.get("eventAcceptedByOrganizer");
                if (eventIds != null && !eventIds.isEmpty()) {
                    // Fetch event details using the event IDs
                    fetchEventDetails(eventIds);
                } else {
                    Toast.makeText(getContext(), "No events found in your accepted list", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error fetching user data", e);
        });
    }

    private void fetchEventDetails(List<String> eventIds) {
        List<Event> tempEventList = new ArrayList<>();
        List<Runnable> imageFetchTasks = new ArrayList<>();

        for (String eventId : eventIds) {
            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String eventName = documentSnapshot.getString("eventName");
                    String eventDescription = documentSnapshot.getString("description");
                    String eventDate = documentSnapshot.getString("eventDate");
                    String imageID = documentSnapshot.getString("imageID");

                    // Prepare the image fetch task
                    String imagePath = "images/events/" + imageID; // Adjust path as needed
                    StorageReference imageRef = storageReference.child(imagePath);

                    imageFetchTasks.add(() -> {
                        try {
                            File localFile = File.createTempFile("event_image", "jpg");
                            imageRef.getFile(localFile)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        synchronized (tempEventList) {
                                            tempEventList.add(new Event(eventName, bitmap, eventDescription, eventDate));
                                        }
                                        checkAndUpdateAdapter(tempEventList, eventIds.size());
                                    })
                                    .addOnFailureListener(e -> {
                                        synchronized (tempEventList) {
                                            tempEventList.add(new Event(eventName, null, eventDescription, eventDate));
                                        }
                                        checkAndUpdateAdapter(tempEventList, eventIds.size());
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error fetching event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching event details", e);
            });
        }

        // Execute image fetch tasks sequentially
        for (Runnable task : imageFetchTasks) {
            task.run();
        }
    }

    private void checkAndUpdateAdapter(List<Event> tempEventList, int expectedSize) {
        // Update the adapter only after all events have been processed
        if (tempEventList.size() == expectedSize) {
            eventList.clear();
            eventList.addAll(tempEventList);
            requireActivity().runOnUiThread(adapter::notifyDataSetChanged);
        }
    }
}