package com.example.eventhub_jigsaw.entrant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.eventhub_jigsaw.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class UserMyprofile extends Fragment implements com.example.eventhub_jigsaw.entrant.UserMyProfileEdit.OnProfileUpdateListener {
    private static final String TAG = "UserMyprofile";
    private TextView Text_username;
    private TextView Text_email;
    private ImageView profileImage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_myprofile, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
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
                            String profilePicturePath = document.getString("profileImageUrl");
                            // Ensure Fragment is still attached
                            if (isAdded()) {
                                Text_username.setText(username);
                                Text_email.setText(email);
                            }
                            // Load the profile image using Glide
                            if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                                fetchProfileImage(profilePicturePath);
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

    private void fetchProfileImage(String profilePicturePath) {
        // Prepend the "images/users/" path to the provided file name
        String fullImagePath = "images/users/" + profilePicturePath;

        // Create a reference to the image in Firebase Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(fullImagePath);

        try {
            final File localFile = File.createTempFile("profile_photo", "jpg");
            imageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profileImage.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Display a toast with the error message
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onProfileUpdate(String newUsername, String newEmail) {
        Text_username.setText(newUsername);
        Text_email.setText(newEmail);
    }
}
