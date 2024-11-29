package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrganizerSampleEntrant extends DialogFragment {

    private TextView sampledUsersTextView;
    private Button confirmButton;

    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_sample_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sampledUsersTextView = view.findViewById(R.id.sampledUsersTextView);
        confirmButton = view.findViewById(R.id.confirmButton);

        db = FirebaseFirestore.getInstance();

        // Get event ID from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("event_id");
            fetchAndSampleUsers();
        } else {
            Toast.makeText(requireContext(), "No event ID provided", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        confirmButton.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void fetchAndSampleUsers() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get waiting list
                        Long maxEntries = documentSnapshot.getLong("maxAttendees");
                        List<String> waitingList = (List<String>) documentSnapshot.get("waitingList");
                        if (waitingList == null || waitingList.isEmpty()) {
                            Toast.makeText(requireContext(), "No users in the waiting list", Toast.LENGTH_SHORT).show();
                            dismiss();
                            return;
                        }

                        // Shuffle and sample 10 users
                        Collections.shuffle(waitingList);
                        List<String> sampledUsers = waitingList.subList(0, Math.min(maxEntries.intValue(), waitingList.size()));

                        // Update Firestore with sampled users
                        db.collection("events").document(eventId)
                                .update("sampledUsers", sampledUsers)
                                .addOnSuccessListener(aVoid -> {
                                    sampledUsersTextView.setText(String.join("\n", sampledUsers));
                                    Toast.makeText(requireContext(), "Sampled users updated", Toast.LENGTH_SHORT).show();

                                    // Update each user's "eventAcceptedByOrganizer" list
                                    for (String userId : sampledUsers) {
                                        db.collection("users").document(userId)
                                                .update("eventAcceptedByOrganizer", FieldValue.arrayUnion(eventId))
                                                .addOnSuccessListener(aVoid2 -> {
                                                    Log.d("OrganizerSampleEntrant", "User " + userId + " updated with event ID " + eventId);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(requireContext(), "Failed to update user " + userId + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to update sampled users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                });
    }
}

