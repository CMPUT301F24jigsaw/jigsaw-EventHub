package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserMyprofile extends Fragment implements com.example.eventhub_jigsaw.entrant.UserMyProfileEdit.OnProfileUpdateListener {
    private static final String TAG = "UserMyprofile";
    private TextView Text_username;
    private TextView Text_email;
    private ImageView profileImage;

    private FirebaseFirestore db;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_myprofile, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Find and set the TextViews
        Text_username = view.findViewById(R.id.username_field);
        Text_email = view.findViewById(R.id.email_field);
        profileImage = view.findViewById(R.id.profile_image);

        // Fetch user data from Firestore
        fetchUserData();

        // Find and handle the Edit button
        Button Button_edit = view.findViewById(R.id.edit_button);
        Button_edit.setOnClickListener(v -> {
            // Show the edit dialog
            com.example.eventhub_jigsaw.entrant.UserMyProfileEdit editDialog = new com.example.eventhub_jigsaw.entrant.UserMyProfileEdit();
            Bundle args = new Bundle();
            args.putString("userID", userID); // Pass the userID
            editDialog.setArguments(args);
            editDialog.setTargetFragment(UserMyprofile.this, 0); // Set target for communication
            editDialog.show(getParentFragmentManager(), "edit_profile_dialog");
        });

        return view;
    }

    // Fetch user data from Firestore
    private void fetchUserData() {
        db.collection("users").document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("name");
                            String email = document.getString("email");
                            String profilePictureUrl = document.getString("profile_picture");
                            // Ensure Fragment is still attached
                            if (isAdded()) {
                                Text_username.setText(username);
                                Text_email.setText(email);
                            }
                            // Load the profile image using Glide
                            if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(profilePictureUrl)
                                        .circleCrop()  // Optionally crop the image to a circle
                                        .into(profileImage);
                                }
                        } else {
                            Log.d(TAG, "No such document");
                            if (isAdded()) {
                                Text_username.setText("User not found");
                                Text_email.setText("Email not found");
                            }
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch document", task.getException());
                    }
                });
    }

    @Override
    public void onProfileUpdate(String newUsername, String newEmail) {
        Text_username.setText(newUsername);
        Text_email.setText(newEmail);
    }
}
