package com.example.eventhub_jigsaw;

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

/**
 * DeleteProfile is a Fragment that displays user profile details and provides
 * functionality to delete the profile.
 */

public class DeleteProfile extends Fragment {

    private ImageView profileImage;
    private TextView usernameText;
    private TextView emailText;
    private Button deleteProfileButton;

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

            // For profileImage, load from resources or URL if available (not included here)
            // Example: profileImage.setImageResource(R.drawable.default_profile_image);
        }

        // Set up the delete button click listener
        deleteProfileButton.setOnClickListener(v -> {
            // Simulate deletion operation (you should implement actual deletion logic here)
            deleteProfile(usernameText.getText().toString());

            // Show a success message
            Toast.makeText(getContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();

            // Go back to the previous fragment after deletion
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    /**
     * Deletes the specified profile.
     *
     * @param username The username of the profile to be deleted.
     */

    // Implement the actual deletion logic
    private void deleteProfile(String username) {
        // Here, you would typically remove the profile from your data source (database, API, etc.)
        // For example:
        // ProfileDatabase.getInstance(getContext()).deleteProfile(username);
    }
}
