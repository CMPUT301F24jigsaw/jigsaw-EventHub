package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        void onProfileUpdate(String newUsername, String newEmail, long newPhone);
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
            String currentUsername = getArguments().getString("currentUsername", "");
            String currentEmail = getArguments().getString("currentEmail", "");
            long currentPhone = getArguments().getLong("currentPhone", 0);

            // Autofill fields
            EditText inputUsername = view.findViewById(R.id.edit_username_field);
            EditText inputEmail = view.findViewById(R.id.edit_email_field);
            EditText phoneInput = view.findViewById(R.id.edit_phone_field);

            inputUsername.setText(currentUsername);
            inputEmail.setText(currentEmail);
            phoneInput.setText(currentPhone == 0 ? "" : String.valueOf(currentPhone));


        }

        // Find input fields and button
        EditText inputUsername = view.findViewById(R.id.edit_username_field);
        EditText inputEmail = view.findViewById(R.id.edit_email_field);
        EditText phoneInput = view.findViewById(R.id.edit_phone_field);
        Button deletePhotoButton = view.findViewById(R.id.delete_photo_button);
        Button saveButton = view.findViewById(R.id.save_button);

        // Handle delete photo button click
        deletePhotoButton.setOnClickListener(v -> removeProfilePhoto());


        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String newUsername = inputUsername.getText().toString();
            String newEmail = inputEmail.getText().toString();
            String phoneText  = phoneInput.getText().toString();
            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                // Show error message if needed
                return;
            }
            try {
                long newPhone = Long.parseLong(phoneText);
                updateFirestoreData(newUsername, newEmail, newPhone);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid phone number.", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        return view;
    }

    private void removeProfilePhoto() {
        if (userID == null || userID.isEmpty()) {
            System.err.println("Error: userID is null or empty. Cannot update Firestore.");
            return;
        }

        db.collection("users").document(userID)
                .update("profileImageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile photo removed successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error removing profile photo: " + e.getMessage());
                });
    }

    /**
     * Updates the user's profile data in Firestore.
     *
     * @param newUsername The updated username.
     * @param newEmail The updated email.
     */
    private void updateFirestoreData(String newUsername, String newEmail, long newPhone) {
        if (userID == null || userID.isEmpty()) {
            System.err.println("Error: userID is null or empty. Cannot update Firestore.");
            return;
        }

        db.collection("users").document(userID)
                .update("name", newUsername, "email", newEmail, "phone", newPhone)
                .addOnSuccessListener(aVoid -> {
                    if (getTargetFragment() instanceof OnProfileUpdateListener) {
                        ((OnProfileUpdateListener) getTargetFragment()).onProfileUpdate(newUsername, newEmail, newPhone);
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating profile: " + e.getMessage());
                });
    }
}