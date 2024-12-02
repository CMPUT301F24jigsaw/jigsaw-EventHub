package com.example.eventhub_jigsaw.organizer;

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

/**
 * OrganizerMyProfile displays the organizer's profile and allows them to edit their information.
 */

public class OrganizerMyProfile extends Fragment implements OrganizerMyProfileEdit.OnProfileUpdateListener {
    private static final String TAG = "OrganizerMyProfile";
    private TextView organizerNameField;
    private TextView organizerEmailField;
    private ImageView organizerProfileImage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String organizerID;

    /**
     * Inflates the layout and initializes the profile fields.
     * @param inflater layout inflater.
     * @param container view container.
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_myprofile, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Find views
        organizerNameField = view.findViewById(R.id.username_field);
        organizerEmailField = view.findViewById(R.id.email_field);
        organizerProfileImage = view.findViewById(R.id.profile_image);

        // Fetch organizer data
        fetchOrganizerData();

        // Handle Edit button
        Button editButton = view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(v -> {
            OrganizerMyProfileEdit editDialog = new OrganizerMyProfileEdit();
            Bundle args = new Bundle();
            args.putString("organizerID", organizerID); // Pass the organizer ID
            editDialog.setArguments(args);
            editDialog.setTargetFragment(OrganizerMyProfile.this, 0);
            editDialog.show(getParentFragmentManager(), "edit_organizer_profile_dialog");
        });

        return view;
    }

    private void fetchOrganizerData() {
        db.collection("users").document(organizerID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String profilePicturePath = document.getString("profileImageUrl");

                            if (isAdded()) {
                                organizerNameField.setText(name);
                                organizerEmailField.setText(email);
                            }

                            if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                                fetchProfileImage(profilePicturePath);
                            }

                        } else {
                            Log.d(TAG, "No such document");
                            if (isAdded()) {
                                organizerNameField.setText("Organizer not found");
                                organizerEmailField.setText("Email not found");
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
                                    // Set the bitmap image to the ImageView
                                    organizerProfileImage.setImageBitmap(bitmap);
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
    public void onProfileUpdate(String newName, String newEmail, long newPhone) {
        organizerNameField.setText(newName);
        organizerEmailField.setText(newEmail);
    }
}
