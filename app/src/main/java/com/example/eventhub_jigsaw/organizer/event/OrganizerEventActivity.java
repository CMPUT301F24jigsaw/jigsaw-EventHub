package com.example.eventhub_jigsaw.organizer.event;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * OrganizerEventActivity used for displaying and managing events organized by a user.
 * Provides functionalities to view event details, add new events, and get event data from DB.
 */

public class OrganizerEventActivity extends Fragment {

    private List<OrganizerEventPage> eventList;
    private OrganizerEventAdapter adapter;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ImageView organizerEventImage;

    /**
     * Creates and inflates the fragment's view with event listing.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_eventspage, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents_organizer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        eventList = new ArrayList<>();
        adapter = new OrganizerEventAdapter(eventList, getChildFragmentManager());
        recyclerView.setAdapter(adapter);
//        organizerEventImage = view.findViewById(R.id.eventImage_organizer);
        // Fetch initial events
        fetchEventsByOrganizer();

        // Add event button logic
        FloatingActionButton addEventButton = view.findViewById(R.id.addButton);
        addEventButton.setOnClickListener(v -> {
            OrganizerAddEvent addEventDialog = new OrganizerAddEvent();
            addEventDialog.setOnEventAddedListener(this::fetchEventsByOrganizer); // Refresh events after adding
            addEventDialog.show(getChildFragmentManager(), "AddEventDialog");
        });

        return view;
    }

    /**
     * Fetches events organized by the current user from Firestore and displays them in a RecyclerView.
     */
    private void fetchEventsByOrganizer() {
        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events")
                .whereEqualTo("organizerID", organizerID)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error fetching events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        List<OrganizerEventPage> tempEventList = new ArrayList<>();
                        List<Runnable> imageFetchTasks = new ArrayList<>();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String eventName = document.getString("eventName");
                            String eventId = document.getString("imageID");
                            String imagePath = "images/events/" + eventId; // Adjust path as needed
                            StorageReference imageRef = storageReference.child(imagePath);

                            // Add a task for each image fetch
                            imageFetchTasks.add(() -> {
                                try {
                                    File localFile = File.createTempFile("event_image", "jpg");
                                    imageRef.getFile(localFile)
                                            .addOnSuccessListener(taskSnapshot -> {
                                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                synchronized (tempEventList) {
                                                    tempEventList.add(new OrganizerEventPage(eventName, bitmap));
                                                }
                                                checkAndUpdateAdapter(tempEventList, querySnapshot.size());
                                            })
                                            .addOnFailureListener(e -> {
                                                synchronized (tempEventList) {
                                                    tempEventList.add(new OrganizerEventPage(eventName, null));
                                                }
                                                checkAndUpdateAdapter(tempEventList, querySnapshot.size());
                                            });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        // Execute image fetch tasks sequentially
                        for (Runnable task : imageFetchTasks) {
                            task.run();
                        }
                    }
                });
    }

    /**
     * Checks if the fetched events match the expected size and updates the RecyclerView adapter.
     *
     * @param tempEventList List of fetched events.
     * @param expectedSize  Number of events expected.
     */
    private void checkAndUpdateAdapter(List<OrganizerEventPage> tempEventList, int expectedSize) {
        // Update the adapter only after all events have been processed
        if (tempEventList.size() == expectedSize) {
            eventList.clear();
            eventList.addAll(tempEventList);
            requireActivity().runOnUiThread(adapter::notifyDataSetChanged);
        }
    }

}