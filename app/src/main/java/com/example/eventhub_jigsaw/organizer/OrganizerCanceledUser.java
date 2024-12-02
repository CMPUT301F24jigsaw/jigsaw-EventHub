package com.example.eventhub_jigsaw.organizer;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
/**
 * OrganizerCanceledUser displays users who have declined an event invitation.
 */
public class OrganizerCanceledUser extends DialogFragment {

    private FirebaseFirestore db;
    private TextView cancledUsersTextView;
    private String eventId;

    /**
     * Inflates the dialog layout and initializes Firebase Firestore and event details.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_cancled_user, container, false);
        cancledUsersTextView = view.findViewById(R.id.selected_users_textview);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve event name from arguments
        Bundle args = getArguments();
        if (args != null) {
            String eventName = args.getString("event_name");
            String organizerId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            // Retrieve event ID based on event name and organizer ID
            fetchEventId(eventName, organizerId);
        } else {
            Toast.makeText(requireContext(), "Event name not provided", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        return view;
    }

    /**
     * Fetches the event ID from the database based on event name and organizer ID.
     */
    private void fetchEventId(String eventName, String organizerId) {
        db.collection("events")
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("organizerID", organizerId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assuming only one result is returned
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            eventId = document.getId();
                            fetchcancledUsers(); // Fetch selected users once event ID is retrieved
                            return; // Exit the loop early
                        }
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                });
    }

    /**
     * Fetches and displays the list of users who declined the event invitation.
     */
    private void fetchcancledUsers() {
        if (eventId == null) {
            Toast.makeText(requireContext(), "Event ID not available", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> cancledUsers = (List<String>) documentSnapshot.get("declinedInvitationUser");
                        if (cancledUsers != null && !cancledUsers.isEmpty()) {
                            // Create a StringBuilder to concatenate user names
                            StringBuilder userNamesBuilder = new StringBuilder();

                            // Counter to track completed tasks
                            final int[] completedTasks = {0};

                            // Fetch each user's name
                            for (String userId : cancledUsers) {
                                db.collection("users").document(userId)
                                        .get()
                                        .addOnSuccessListener(userDocument -> {
                                            if (userDocument.exists()) {
                                                String userName = userDocument.getString("name"); // Assuming 'name' field exists
                                                if (userName != null) {
                                                    userNamesBuilder.append(userName).append("\n");
                                                }
                                            }
                                            // Increment completed tasks
                                            completedTasks[0]++;
                                            // Check if all tasks are completed
                                            if (completedTasks[0] == cancledUsers.size()) {
                                                // Update the TextView with concatenated names
                                                cancledUsersTextView.setText(userNamesBuilder.toString().trim());
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(requireContext(), "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            // Increment completed tasks even on failure
                                            completedTasks[0]++;
                                            if (completedTasks[0] == cancledUsers.size()) {
                                                cancledUsersTextView.setText(userNamesBuilder.toString().trim());
                                            }
                                        });
                            }
                        } else {
                            cancledUsersTextView.setText("No registered users found.");
                        }
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
