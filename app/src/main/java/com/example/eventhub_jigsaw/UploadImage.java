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

    public void uploadImage(Uri fileUri, OnUploadCompleteListener listener) {
        if (fileUri == null) {
            listener.onFailure("No file selected.");
            return;
        }

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        UploadTask uploadTask = ref.putFile(fileUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        listener.onSuccess(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
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
