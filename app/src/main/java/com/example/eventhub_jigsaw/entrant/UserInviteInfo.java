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

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * UserInviteInfo handles displaying the details of an event invitation and provides options to accept or decline it.
 */

public class UserInviteInfo extends Fragment {

    private TextView eventNameTextView;
    private TextView eventDateTextView;
    private TextView eventDescriptionTextView;
    private Button acceptButton;
    private Button declineButton;
    private FirebaseFirestore db;
    private String userId;
    private String eventID;
    private String eventName;
    private ImageView eventImage;
    String imageURL;

    /**
     * Initializes the fragment and sets up UI elements.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_inviteinfo, container, false);
        db = FirebaseFirestore.getInstance();

        userId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize views
        eventNameTextView = view.findViewById(R.id.eventnameInfo_user);
        eventDateTextView = view.findViewById(R.id.eventdate_user);
        eventDescriptionTextView = view.findViewById(R.id.eventdescription_user);
        eventImage = view.findViewById(R.id.eventInfoImage_user);

        acceptButton = view.findViewById(R.id.AcceptButton);
        declineButton = view.findViewById(R.id.DeclineButton);

        // Retrieve data from the bundle
        if (getArguments() != null) {
            eventName = getArguments().getString("event_name");
            String eventDescription = getArguments().getString("event_description");
            String eventDate = getArguments().getString("event_date");
            eventID = getArguments().getString("event_id");
            imageURL = getArguments().getString("event_image_url");

            // Set the data to views
            eventNameTextView.setText(eventName);
            eventDateTextView.setText(eventDate);
            eventDescriptionTextView.setText(eventDescription);
        }

        if (eventID != null && !eventID.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images/events/" + eventID);

            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                loadImageWithoutGlide(eventImage, downloadUrl);
            }).addOnFailureListener(e -> {
                Log.e("FirebaseStorage", "Error fetching download URL", e);

            });
        }

        // Handle Accept Button Click
        acceptButton.setOnClickListener(v -> {
            // TODO: Add logic to handle the event acceptance
            // For now, just navigate to the loading screen
            acceptEvent(eventID);
        });

        // Handle Decline Button Click
        declineButton.setOnClickListener(v -> {
            // Navigate to the loading screen
            declineEvent(eventID);
        });

        return view;
    }


    /**
     * Accepts the event and updates DB with the new status.
     * @param eventID The ID of the event being accepted.
     */
    private void acceptEvent(String eventID) {
        // Add eventID to user's registeredEvents list
        db.collection("users").document(userId)
                .update("registeredEvents", FieldValue.arrayUnion(eventID),
                        "eventAcceptedByOrganizer", FieldValue.arrayRemove(eventID),
                        "notifications", FieldValue.arrayUnion("You have accepted the invitation for " + eventName))
                .addOnSuccessListener(unused -> {
                    // Add userId to event's registeredUsers list
                    db.collection("events").document(eventID)
                            .update("registeredUsers", FieldValue.arrayUnion(userId))
                            .addOnSuccessListener(unused2 -> {
                                dismissFragment(); // Navigate back
                            })
                            .addOnFailureListener(e -> showError("Failed to update event's registered users."));
                })
                .addOnFailureListener(e -> showError("Failed to accept the event."));
    }

    /**
     * Declines the event and updates DB with the new status.
     * @param eventID The ID of the event being accepted.
     */
    private void declineEvent(String eventID) {
        // Remove eventID from user's eventAcceptedByOrganizer list
        db.collection("users").document(userId)
                .update("eventAcceptedByOrganizer", FieldValue.arrayRemove(eventID), "notifications", FieldValue.arrayUnion("You have declined the invitation for " + eventName))
                .addOnSuccessListener(unused -> {
                    // Remove userId from event's sampledUsers list
                    db.collection("events").document(eventID)
                            .update("sampledUsers", FieldValue.arrayRemove(userId),
                            "declinedInvitationUser", FieldValue.arrayUnion(userId))
                            .addOnSuccessListener(unused2 -> {
                                dismissFragment(); // Navigate back
                            })
                            .addOnFailureListener(e -> showError("Failed to update event's sampled users."));
                })
                .addOnFailureListener(e -> showError("Failed to decline the event."));
    }

    /**
     * Shows an error message to the user.
     * @param message The error message to be displayed.
     */
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Dismisses the current fragment.
     */
    private void dismissFragment() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void loadImageWithoutGlide(ImageView imageView, String imageUrl) {
        new Thread(() -> {
            try {
                // Open a connection to the URL
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                // Decode the input stream to a Bitmap
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Set the Bitmap to the ImageView on the main thread
                imageView.post(() -> imageView.setImageBitmap(bitmap));

            } catch (Exception e) {
                Log.e("LoadImage", "Error loading image", e);
                // Set fallback image on error
            }
        }).start();
    }
}
