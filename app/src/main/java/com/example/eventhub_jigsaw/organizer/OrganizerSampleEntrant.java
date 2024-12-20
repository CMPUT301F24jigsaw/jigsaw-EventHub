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

/**
 * OrganizerSampleEntrant samples entrants from a waiting list for an event.
 */

public class OrganizerSampleEntrant extends DialogFragment {

    private TextView sampledUsersTextView;
    private Button confirmButton;

    private FirebaseFirestore db;
    private String eventId;
    private String name;

    /**
     * Inflates the layout for sampling and displaying users.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_sample_user, container, false);
    }

    /**
     * Fetches users from the waiting list and selects a random sample.
     */
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
                        // Get event details
                        name = documentSnapshot.getString("eventName");
                        Long maxEntries = documentSnapshot.getLong("maxAttendees");
                        List<String> waitingList = (List<String>) documentSnapshot.get("waitingList");
                        List<String> declinedInvitationUser = (List<String>) documentSnapshot.get("declinedInvitationUser");

                        if (waitingList == null || waitingList.isEmpty()) {
                            Toast.makeText(requireContext(), "No users in the waiting list", Toast.LENGTH_SHORT).show();
                            dismiss();
                            return;
                        }

                        // Determine the size for sampling
                        int sampleSize = maxEntries.intValue();
                        if (declinedInvitationUser != null && !declinedInvitationUser.isEmpty()) {
                            sampleSize = Math.min(declinedInvitationUser.size(), waitingList.size());
                        } else {
                            sampleSize = Math.min(maxEntries.intValue(), waitingList.size());
                        }

                        // Shuffle and sample users
                        Collections.shuffle(waitingList);
                        List<String> sampledUsers = waitingList.subList(0, sampleSize);

                        // Filter users who didn't win
                        List<String> remainingUsers = new ArrayList<>(waitingList);
                        remainingUsers.removeAll(sampledUsers);

                        // Update Firestore with sampled users and remove from waitingList
                        db.collection("events").document(eventId)
                                .update("sampledUsers", sampledUsers,
                                        "waitingList", FieldValue.arrayRemove(sampledUsers.toArray(new String[0])))
                                .addOnSuccessListener(aVoid -> {
                                    sampledUsersTextView.setText(String.join("\n", sampledUsers));
                                    Toast.makeText(requireContext(), "Sampled users updated", Toast.LENGTH_SHORT).show();

                                    // Notify sampled users
                                    for (String userId : sampledUsers) {
                                        db.collection("users").document(userId)
                                                .update("eventAcceptedByOrganizer", FieldValue.arrayUnion(eventId),
                                                        "waitingList", FieldValue.arrayRemove(eventId),
                                                        "notifications", FieldValue.arrayUnion("You have won the lottery! Accept or Decline your invitation for " + name))
                                                .addOnSuccessListener(aVoid2 -> {
                                                    Log.d("OrganizerSampleEntrant", "User " + userId + " updated with event ID " + eventId);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(requireContext(), "Failed to update user " + userId + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }

                                    // Notify remaining users
                                    for (String userId : remainingUsers) {
                                        db.collection("users").document(userId)
                                                .update("notifications", FieldValue.arrayUnion("You lost the lottery! However, you are still on the waiting list for event: " + name))
                                                .addOnSuccessListener(aVoid3 -> {
                                                    Log.d("WaitingListNotification", "Notification sent to user: " + userId);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("WaitingListNotification", "Failed to send notification to user: " + userId, e);
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

