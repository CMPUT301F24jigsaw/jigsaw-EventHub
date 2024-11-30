package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * DeleteProfile is a Fragment that displays user profile details and provides
 * functionality to delete the profile.
 */

public class DeleteProfile extends Fragment {

    private ImageView profileImage;
    private TextView usernameText;
    private TextView emailText;
    private Button deleteProfileButton;
    private FirebaseFirestore db;

    /**
     * Called to initialize the fragment's user interface view.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view to attach the fragment's UI.
     * @param savedInstanceState Bundle containing the fragment's previously saved state.
     * @return The View for the fragment's UI, or null.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.delete_page, container, false);


        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profile_image);
        usernameText = view.findViewById(R.id.username_text);
        emailText = view.findViewById(R.id.email_text);
        deleteProfileButton = view.findViewById(R.id.delete_profile_button);

        // Retrieve profile data from arguments (if available)
        Bundle bundle = getArguments();
        if (bundle != null) {
            String username = bundle.getString("username", "Username");
            String email = bundle.getString("email", "Email");

            // Set profile data to TextViews
            usernameText.setText(username);
            emailText.setText(email);

            // Set up the delete button click listener
            deleteProfileButton.setOnClickListener(v -> {
                if (username != null) {
                    deleteProfile(username); // Call method to delete the profile from Firestore
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve username for deletion.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No profile data available.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    /**
     * Deletes the specified profile.
     *
     * @param username The username of the profile to be deleted.
     */

    // Implement the actual deletion logic
    // Inside the DeleteProfile fragment
    private void deleteProfile(String username) {
        db.collection("users").whereEqualTo("name", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assuming there’s only one document with the given username
                        String documentId = querySnapshot.getDocuments().get(0).getId();

                        // Delete the document using its document ID
                        db.collection("users").document(documentId)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "Profile deleted successfully.", Toast.LENGTH_SHORT).show();

                                    // Navigate back to the previous fragment
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to delete profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "No profile found with that username.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to find profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
