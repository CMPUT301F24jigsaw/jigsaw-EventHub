package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerInvitedUsers extends DialogFragment {

    private TextView invitedUsersTextView;
    private FirebaseFirestore db;
    private String eventID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the dialog
        View view = inflater.inflate(R.layout.organizer_invited_user, container, false);

        invitedUsersTextView = view.findViewById(R.id.textView_invited_users);
        db = FirebaseFirestore.getInstance();

        // Close button functionality
        view.findViewById(R.id.button_close).setOnClickListener(v -> dismiss());

        // Fetch the event ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            eventID = args.getString("event_id");
            if (eventID != null) {
                fetchInvitedUsers(eventID);
            } else {
                Toast.makeText(getContext(), "Event ID not provided", Toast.LENGTH_SHORT).show();
            }
        }

        view.findViewById(R.id.button_remove_all).setOnClickListener(v -> {
            removeAllInvitedUsers(eventID);
        });

        return view;
    }

    private void fetchInvitedUsers(String eventID) {
        db.collection("events")
                .document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> sampledUsers = (List<String>) documentSnapshot.get("sampledUsers");
                        if (sampledUsers != null && !sampledUsers.isEmpty()) {
                            // Fetch user names for each user ID
                            fetchUserNames(sampledUsers);
                        } else {
                            invitedUsersTextView.setText("No invited users found.");
                        }
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching users: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchUserNames(List<String> userIds) {
        StringBuilder usersText = new StringBuilder();

        for (String userId : userIds) {
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String userName = userDoc.getString("name"); // Assuming "name" is the field for the user's name
                            if (userName != null) {
                                usersText.append(userName).append("\n");
                            } else {
                                usersText.append("Unknown User (ID: ").append(userId).append(")\n");
                            }
                            // Update the TextView after each successful fetch
                            invitedUsersTextView.setText(usersText.toString());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Append an error message for the specific user ID
                        usersText.append("Error fetching user (ID: ").append(userId).append(")\n");
                        invitedUsersTextView.setText(usersText.toString());
                    });
        }

    }

    private void removeAllInvitedUsers(String eventID) {
        db.collection("events")
                .document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> sampledUsers = (List<String>) documentSnapshot.get("sampledUsers");
                        if (sampledUsers != null && !sampledUsers.isEmpty()) {
                            // Remove eventID from eventAcceptedByOrganizer for each user
                            for (String userId : sampledUsers) {
                                // Update the user document
                                db.collection("users")
                                        .document(userId)
                                        .update("eventAcceptedByOrganizer", FieldValue.arrayRemove(eventID))
                                        .addOnSuccessListener(aVoid -> {
                                            // Optional: Log success for individual user updates
                                        })
                                        .addOnFailureListener(e -> {
                                            // Log failure for specific user update
                                            Toast.makeText(getContext(), "Failed to update user " + userId + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }

                            // Move all sampledUsers to declinedInvitationUser in the event document
                            db.collection("events")
                                    .document(eventID)
                                    .update(
                                            "declinedInvitationUser", FieldValue.arrayUnion(sampledUsers.toArray(new String[0])),
                                            "sampledUsers", new ArrayList<>()
                                    )
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "All invited users cleared and moved to declined.", Toast.LENGTH_SHORT).show();
                                        // Clear the TextView as well
                                        invitedUsersTextView.setText("No invited users found.");
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update event document: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(getContext(), "No users to process.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }




    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

}
