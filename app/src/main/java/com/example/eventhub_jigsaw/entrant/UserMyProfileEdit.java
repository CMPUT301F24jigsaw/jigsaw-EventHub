package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.provider.Settings;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class UserMyProfileEdit extends DialogFragment {

    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newUsername, String newEmail, long newPhone);
    }

    private FirebaseFirestore db;
    private String userID;

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
        TextView phoneInput = view.findViewById(R.id.edit_phone_field);
        Button saveButton = view.findViewById(R.id.save_button);

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
        });

        return view;
    }

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