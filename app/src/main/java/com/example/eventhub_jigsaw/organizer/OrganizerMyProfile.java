package com.example.eventhub_jigsaw.organizer;

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

public class OrganizerMyProfile extends Fragment implements OrganizerMyProfileEdit.OnProfileUpdateListener {
    private static final String TAG = "OrganizerMyProfile";
    private TextView organizerNameField;
    private TextView organizerEmailField;
    private ImageView organizerProfileImage;

    private FirebaseFirestore db;
    private String organizerID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_myprofile, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
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
                            String profilePictureUrl = document.getString("profile_picture");

                            if (isAdded()) {
                                organizerNameField.setText(name);
                                organizerEmailField.setText(email);
                            }

                            if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(profilePictureUrl)
                                        .circleCrop()
                                        .into(organizerProfileImage);
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

    @Override
    public void onProfileUpdate(String newName, String newEmail) {
        organizerNameField.setText(newName);
        organizerEmailField.setText(newEmail);
    }
}
