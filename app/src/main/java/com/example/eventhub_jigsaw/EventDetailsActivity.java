package com.example.eventhub_jigsaw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends DialogFragment {

    private TextView eventNameTextView, eventDescriptionTextView;
    private ImageView eventQrImageView;
    private Button buttonJoinWaitlist;

    private FirebaseFirestore db;
    private String eventId;
    private String userId;
    private String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qr_scan_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        eventNameTextView = view.findViewById(R.id.eventNameTextView);
        eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        eventQrImageView = view.findViewById(R.id.eventQrImageView);
        buttonJoinWaitlist = view.findViewById(R.id.buttonJoinWaitlist);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve user ID safely
        userId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Retrieve event ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString("event_id");
            if (eventId != null) {
                fetchEventDetails(eventId);
            } else {
                Toast.makeText(requireContext(), "Invalid event ID", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        } else {
            Toast.makeText(requireContext(), "No event data provided", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        // Handle Join Waitlist button click
        buttonJoinWaitlist.setOnClickListener(v -> {
            buttonJoinWaitlist.setEnabled(false); // Disable button to prevent duplicate actions
            joinWaitlist();
        });
    }

    private void fetchEventDetails(String eventId) {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        name = documentSnapshot.getString("eventName");
                        String description = documentSnapshot.getString("description");
                        String qrCodeBase64 = documentSnapshot.getString("qrCode");

                        eventNameTextView.setText(name);
                        eventDescriptionTextView.setText(description);

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

    private void joinWaitlist() {
        if (eventId == null || userId == null) {
            Toast.makeText(requireContext(), "Event or user information missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update event waitlist
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.update("waitingList", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    // Update user waitlist
                    DocumentReference userRef = db.collection("users").document(userId);
                    userRef.update("waitingList", FieldValue.arrayUnion(eventId),
                                    "notifications", FieldValue.arrayUnion("You have joined the waiting list for " + name))
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(requireContext(), "Added to waitlist!", Toast.LENGTH_SHORT).show();
                                dismiss(); // Close the dialog
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed to update user waitlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                buttonJoinWaitlist.setEnabled(true); // Re-enable button
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to update event waitlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    buttonJoinWaitlist.setEnabled(true); // Re-enable button
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
