package com.example.eventhub_jigsaw;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import com.bumptech.glide.Glide;

public class SelectImage {

    private final ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri selectedImageUri;
    private final ImageView imageView;
    private Button uploadButton; // Optional: Button for uploading image, but not mandatory

    public SelectImage(ActivityResultLauncher<Intent> activityResultLauncher, ImageView imageView) {
        this.activityResultLauncher = activityResultLauncher;
        this.imageView = imageView;
        this.uploadButton = null;  // Removed dependency on the upload button being provided
    }

    // Function to open the gallery and select an image
    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");  // Filter to show only images
        activityResultLauncher.launch(intent); // Launch the gallery activity
    }

    // The result of the image selection is handled here
    public ActivityResultCallback<ActivityResult> getActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        Glide.with(imageView.getContext()).load(selectedImageUri).into(imageView);
                    } else {
                        Toast.makeText(imageView.getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(imageView.getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }
}
