package com.example.eventhub_jigsaw;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

/**
 * UploadImage handles the uploading of images to DB Storage.
 */

public class UploadImage {

    private StorageReference storageReference;

    /**
     * Constructor to initialize the UploadImage class with DB reference.
     */
    public UploadImage() {
        storageReference = FirebaseStorage.getInstance().getReference(); // Initialize Firebase Storage
    }


    /**
     * Uploads a user's image to DB Storage.
     * @param fileUri The URI of the image.
     * @param listener For the upload completion.
     * @param deviceId The unique ID of the device.
     */
    public void uploadImage(Uri fileUri, OnUploadCompleteListener listener, String deviceId) {
        if (fileUri == null) {
            listener.onFailure("No file selected.");
            return;
        }

        StorageReference ref = storageReference.child("images/users/"  + deviceId);
        UploadTask uploadTask = ref.putFile(fileUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Return the deviceId on successful upload
                        listener.onSuccess(deviceId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Uploads an event image to DB Storage.
     * @param fileUri The URI of the image.
     * @param listener For the upload completion.
     * @param eventId The unique ID of the event.
     */
    public void uploadImageEvents(Uri fileUri, OnUploadCompleteListener listener, String eventId) {
        if (fileUri == null) {
            listener.onFailure("No file selected.");
            return;
        }

        // Generate a random eventId

        StorageReference ref = storageReference.child("images/events/" + eventId);
        UploadTask uploadTask = ref.putFile(fileUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Return the generated eventId on successful upload
                        listener.onSuccess(eventId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }


    /**
     * Interface for upload completion listener.
     */
    public interface OnUploadCompleteListener {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }
}
