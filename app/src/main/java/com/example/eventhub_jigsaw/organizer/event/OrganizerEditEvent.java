package com.example.eventhub_jigsaw.organizer.event;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.SelectImage;
import com.example.eventhub_jigsaw.UploadImage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerEditEvent extends DialogFragment {

    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView eventImageView;
    private Button selectImageButton;
    private Uri eventImageUri;
    private SelectImage selectImage;
    private UploadImage uploadImage;
    String eventName, eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_edit_poster, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventImageView = view.findViewById(R.id.eventImage);  // ImageView to display selected image
        selectImageButton = view.findViewById(R.id.editImageButton);  // Button to trigger image selection

        // Initialize UploadImage and SelectImage classes
        uploadImage = new UploadImage(); // Make sure you are passing the correct context
        selectImage = new SelectImage(activityResultLauncher, eventImageView);

        db = FirebaseFirestore.getInstance();

        // Set click listener on the button to select an image
        selectImageButton.setOnClickListener(v -> selectImage.selectImage());

        // Retrieve the event data passed in as arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            eventName = arguments.getString("event_name");
            eventId = arguments.getString("event_id");
        }

        // Save the event when clicking the save button
        view.findViewById(R.id.saveEventButton).setOnClickListener(v -> {
            if (eventImageUri != null) {
                // Upload the new image to Firebase Storage
                uploadImage.uploadImageEvents(eventImageUri, new UploadImage.OnUploadCompleteListener() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        // After successful upload, update the Firestore document with the new image URL
                        db.collection("events").document(eventId)
                                .update("imageID", imageUrl)
                                .addOnSuccessListener(aVoid -> showToast("Event updated with new image."))
                                .addOnFailureListener(e -> showToast("Failed to update event with new image: " + e.getMessage()));
                        dismiss();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        showToast("Failed to upload image: " + errorMessage);
                    }
                }, eventId);
            } else {
                showToast("No image selected to update.");
            }
        });
    }

    // Launcher for the image selection activity
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                selectImage.getActivityResultCallback().onActivityResult(result);
                eventImageUri = selectImage.getSelectedImageUri(); // Update the URI after selecting the image
            }
    );

    // Helper method to display a toast message
    private void showToast(String message) {
        if (getContext() != null) { // Check if the context is available
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("OrganizerEditEvent", "Context is null, cannot show toast.");
        }
    }

}
