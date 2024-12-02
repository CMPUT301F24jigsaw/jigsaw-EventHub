package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerViewWaitlist extends DialogFragment {

    private RecyclerView recyclerView;
    private WaitlistAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private String eventId; // Store eventId
    private boolean geolocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_view_waitlist, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewWaitlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter but don't set it until users are fetched
        adapter = new WaitlistAdapter(getContext(), userList, false);

        // Retrieve the eventId passed in arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("event_id");
            fetchWaitlistUsers();
        } else {
            Log.e("OrganizerViewWaitlist", "Event ID not provided. Closing dialog.");
            dismiss();
        }
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.button_sample_User).setOnClickListener(v -> {
            OrganizerSampleEntrant sampleEntrant = new OrganizerSampleEntrant();
            Bundle bundle = new Bundle();
            bundle.putString("event_id", eventId);
            sampleEntrant.setArguments(bundle);
            sampleEntrant.show(getParentFragmentManager(), "sampleEntrant");
        });

        view.findViewById(R.id.button_close).setOnClickListener(v -> dismiss());

        return view;
    }

    private void fetchWaitlistUsers() {
        if (eventId == null) {
            Log.e("OrganizerViewWaitlist", "Event ID is null.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> waitingList = (List<String>) documentSnapshot.get("waitingList");
                Boolean geoEnabled = documentSnapshot.getBoolean("geolocation");
                geolocation = geoEnabled != null && geoEnabled;

                // Update the adapter's geolocation setting
                adapter = new WaitlistAdapter(getContext(), userList, geolocation);
                recyclerView.setAdapter(adapter);

                if (waitingList != null && !waitingList.isEmpty()) {
                    Log.d("OrganizerViewWaitlist", "Waiting List: " + waitingList);
                    for (String userId : waitingList) {
                        fetchUserDetails(userId);
                    }
                } else {
                    Log.e("OrganizerViewWaitlist", "Waiting list is empty or null.");
                }
            } else {
                Log.e("OrganizerViewWaitlist", "Event document not found.");
            }
        }).addOnFailureListener(e -> Log.e("OrganizerViewWaitlist", "Error fetching event: " + e.getMessage()));
    }

    private void fetchUserDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnSuccessListener(userSnapshot -> {
            if (userSnapshot.exists()) {
                Log.d("OrganizerViewWaitlist", "User found: " + userId);

                User user = new User(
                        userSnapshot.getString("name"),
                        userSnapshot.getString("email"),
                        userSnapshot.getString("userID"),
                        userSnapshot.getLong("phone").longValue(),
                        User.Role.valueOf(userSnapshot.getString("role"))
                );

                if (geolocation) {
                    user.setLongitude(userSnapshot.getDouble("longitude"));
                    user.setLatitude(userSnapshot.getDouble("latitude"));
                }

                userList.add(user);

                // Notify adapter only once after fetching all users

                adapter.notifyDataSetChanged();
            } else {
                Log.e("OrganizerViewWaitlist", "User document not found: " + userId);
            }
        }).addOnFailureListener(e -> Log.e("OrganizerViewWaitlist", "Error fetching user: " + e.getMessage()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
