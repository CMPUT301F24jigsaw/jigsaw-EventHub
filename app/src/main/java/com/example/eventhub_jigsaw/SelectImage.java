package com.example.eventhub_jigsaw;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import com.bumptech.glide.Glide;

public class SelectImage {

    private final ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri selectedImageUri;
    private final ImageView imageView;

    public SelectImage(ActivityResultLauncher<Intent> activityResultLauncher, ImageView imageView) {
        this.activityResultLauncher = activityResultLauncher;
        this.imageView = imageView;
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
                    Glide.with(imageView.getContext()).load(selectedImageUri).into(imageView);
                    // Optionally, you can also enable an "Upload" button here if needed
                } else {
                    Toast.makeText(imageView.getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }
}
