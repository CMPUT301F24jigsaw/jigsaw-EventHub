package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
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
 * OrganizerMyProfileEdit edits the organizer's profile details.
 */
public class OrganizerMyProfileEdit extends DialogFragment {

    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newName, String newEmail);
    }

    private FirebaseFirestore db;
    private String organizerID;

    /**
     * Inflates the layout and sets up profile editing features.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_myprofile_edit, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve organizerID from arguments
        if (getArguments() != null) {
            organizerID = getArguments().getString("organizerID");
        }

        // Find views
        TextView nameInput = view.findViewById(R.id.edit_organizer_name_field);
        TextView emailInput = view.findViewById(R.id.edit_organizer_email_field);
        Button saveButton = view.findViewById(R.id.organizer_save_button);

        // Handle save button
        saveButton.setOnClickListener(v -> {
            String newName = nameInput.getText().toString();
            String newEmail = emailInput.getText().toString();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                // Optionally show an error message
                return;
            }

            updateFirestoreData(newName, newEmail);
        });

        return view;
    }

    /**
     * Updates the organizer's profile information in Firestore.
     */
    private void updateFirestoreData(String newName, String newEmail) {
        if (organizerID == null || organizerID.isEmpty()) {
            System.err.println("Error: organizerID is null or empty. Cannot update Firestore.");
            return;
        }

        db.collection("users").document(organizerID)
                .update("name", newName, "email", newEmail)
                .addOnSuccessListener(aVoid -> {
                    if (getTargetFragment() instanceof OnProfileUpdateListener) {
                        ((OnProfileUpdateListener) getTargetFragment()).onProfileUpdate(newName, newEmail);
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating organizer profile: " + e.getMessage());
                });
    }
}
