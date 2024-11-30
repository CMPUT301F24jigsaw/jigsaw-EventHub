package com.example.eventhub_jigsaw;

import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadImage {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    public interface OnUploadCompleteListener {
        void onSuccess(String imageUrl);

        void onFailure(String errorMessage);
    }

    public void uploadImage(Uri imageUri, OnUploadCompleteListener listener) {
        if (imageUri != null) {
            // Create a reference to where the image should be stored in Firebase Storage
            StorageReference imageRef = storageReference.child("profile_images/" + System.currentTimeMillis() + ".jpg");

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Image uploaded successfully, return the download URL
                                listener.onSuccess(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure in getting download URL
                                listener.onFailure("Failed to get image URL: " + e.getMessage());
                            }))
                    .addOnFailureListener(e -> {
                        // Handle failure in uploading the image
                        listener.onFailure("Image upload failed: " + e.getMessage());
                    });
        } else {
            listener.onFailure("No image selected");
        }
    }
}
