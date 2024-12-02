package com.example.eventhub_jigsaw;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class UploadImage {

    private StorageReference storageReference;

    public UploadImage() {
        storageReference = FirebaseStorage.getInstance().getReference(); // Initialize Firebase Storage
    }

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


    public interface OnUploadCompleteListener {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }
}
