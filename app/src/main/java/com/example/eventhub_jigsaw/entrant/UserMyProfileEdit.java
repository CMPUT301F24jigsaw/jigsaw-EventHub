package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * UserMyProfileEdit is used for editing user's profile information.
 * Allows the user to update their username and email.
 */

public class UserMyProfileEdit extends DialogFragment {

    /**
     * Interface to notify the parent fragment/activity when the profile is updated.
     */
    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newUsername, String newEmail);
    }

    private FirebaseFirestore db;
    private String userID;

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater For inflate views.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Contains previous state data.
     * @return The inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_myprofile_edit, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve userID from arguments
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }

        // Find input fields and button
        TextView inputUsername = view.findViewById(R.id.edit_username_field);
        TextView inputEmail = view.findViewById(R.id.edit_email_field);
        Button saveButton = view.findViewById(R.id.save_button);

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String newUsername = inputUsername.getText().toString();
            String newEmail = inputEmail.getText().toString();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                // Show error message if needed
                return;
            }

            updateFirestoreData(newUsername, newEmail);
        });

        return view;
    }

    /**
     * Updates the user's profile data in Firestore.
     *
     * @param newUsername The updated username.
     * @param newEmail The updated email.
     */
    private void updateFirestoreData(String newUsername, String newEmail) {
        if (userID == null || userID.isEmpty()) {
            System.err.println("Error: userID is null or empty. Cannot update Firestore.");
            return;
        }

        db.collection("users").document(userID)
                .update("name", newUsername, "email", newEmail)
                .addOnSuccessListener(aVoid -> {
                    if (getTargetFragment() instanceof OnProfileUpdateListener) {
                        ((OnProfileUpdateListener) getTargetFragment()).onProfileUpdate(newUsername, newEmail);
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating profile: " + e.getMessage());
                });
    }
}