package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminRemoveEventsDetail extends DialogFragment {

    private Button QrRemove, ImageRemove, EventRemove;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_remove_events_detail, container, false);

        // Initialize buttons
        QrRemove = view.findViewById(R.id.remove_QR);
        ImageRemove = view.findViewById(R.id.remove_image);
        EventRemove = view.findViewById(R.id.remove_event);

        // Get the bundled event_id
        Bundle args = getArguments();
        String documentId = args != null ? args.getString("event_id") : null;

        if (documentId == null) {
            Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            dismiss(); // Close the dialog if event_id is missing
            return view;
        }

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(documentId);

        // Set onClickListeners for buttons
        QrRemove.setOnClickListener(v -> {
            eventRef.update("qrCode", null)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "QR Code removed successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error removing QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        ImageRemove.setOnClickListener(v -> {
            eventRef.update("imageID", null)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event image removed successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error removing event image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        EventRemove.setOnClickListener(v -> {
            eventRef.delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event removed successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error removing event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        return view;
    }
}
