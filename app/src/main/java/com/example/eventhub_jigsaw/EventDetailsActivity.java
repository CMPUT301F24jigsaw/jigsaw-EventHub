package com.example.eventhub_jigsaw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends DialogFragment {

    private TextView eventNameTextView, eventDescriptionTextView;
    private ImageView eventQrImageView;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the dialog fragment
        return inflater.inflate(R.layout.qr_scan_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        eventNameTextView = view.findViewById(R.id.eventNameTextView);
        eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        eventQrImageView = view.findViewById(R.id.eventQrImageView);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve event ID passed through arguments
        Bundle args = getArguments();
        if (args != null) {
            String eventId = args.getString("event_id");
            if (eventId != null) {
                fetchEventDetails(eventId); // Fetch and display event details
            } else {
                Toast.makeText(requireContext(), "Invalid event ID", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        } else {
            Toast.makeText(requireContext(), "No event data provided", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private void fetchEventDetails(String eventId) {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve and display event details
                        String name = documentSnapshot.getString("eventName");
                        String description = documentSnapshot.getString("description");
                        String qrCodeBase64 = documentSnapshot.getString("qrCode");

                        eventNameTextView.setText(name);
                        eventDescriptionTextView.setText(description);

                        // Decode and display the QR code if available
                        if (qrCodeBase64 != null) {
                            Bitmap qrCodeBitmap = decodeBase64ToBitmap(qrCodeBase64);
                            eventQrImageView.setImageBitmap(qrCodeBitmap);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                });
    }

    private Bitmap decodeBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // Customize dialog appearance if needed
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
