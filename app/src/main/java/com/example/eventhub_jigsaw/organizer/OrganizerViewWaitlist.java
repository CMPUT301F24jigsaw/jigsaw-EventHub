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
import com.example.eventhub_jigsaw.organizer.WaitlistAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerViewWaitlist extends DialogFragment {

    private RecyclerView recyclerView;
    private WaitlistAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private String eventId; // Store eventId

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_view_waitlist, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewWaitlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WaitlistAdapter(userList);
        recyclerView.setAdapter(adapter);

        // Retrieve the eventId passed in arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("event_id");
            fetchWaitlistUsers();
        } else {
            // If no eventId is provided, dismiss the dialog or show an error
            dismiss();
        }

        view.findViewById(R.id.button_sample_User).setOnClickListener(v -> {
            OrganizerSampleEntrant sampleEntrant = new OrganizerSampleEntrant();
            Bundle bundle = new Bundle();
            bundle.putString("event_id", eventId);
            sampleEntrant.setArguments(bundle);
            sampleEntrant.show(getParentFragmentManager(), "sampleEntrant");
        });

        view.findViewById(R.id.button_close).setOnClickListener(V -> {
            dismiss();
        });

        return view;
    }

    private void fetchWaitlistUsers() {
        if (eventId == null) {
            Log.e("OrganizerViewWaitlist", "Event ID is null.");
            return; // Exit if eventId is not set
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> waitingList = (List<String>) documentSnapshot.get("waitingList");

                if (waitingList != null && !waitingList.isEmpty()) {
                    Log.e("OrganizerViewWaitlist", "Waiting List: " + waitingList);

                    for (String userId : waitingList) {
                        db.collection("users").document(userId).get().addOnSuccessListener(userSnapshot -> {
                            if (userSnapshot.exists()) {
                                Log.e("OrganizerViewWaitlist", "User found: " + userId);

                                User user = new User(
                                        userSnapshot.getString("name"),
                                        userSnapshot.getString("email"),
                                        userSnapshot.getString("userID"),
                                        userSnapshot.getLong("phone").intValue(),
                                        User.Role.valueOf(userSnapshot.getString("role"))
                                );

                                userList.add(user);
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("OrganizerViewWaitlist", "User document not found: " + userId);
                            }
                        }).addOnFailureListener(e -> Log.e("OrganizerViewWaitlist", "Error fetching user: " + e.getMessage()));
                    }
                } else {
                    Log.e("OrganizerViewWaitlist", "Waiting list is empty or null.");
                }
            } else {
                Log.e("OrganizerViewWaitlist", "Event document not found.");
            }
        }).addOnFailureListener(e -> Log.e("OrganizerViewWaitlist", "Error fetching event: " + e.getMessage()));
    }@Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }



}
