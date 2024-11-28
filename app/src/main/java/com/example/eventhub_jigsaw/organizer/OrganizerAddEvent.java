package com.example.eventhub_jigsaw.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

public class OrganizerAddEvent extends DialogFragment {

    private FirebaseFirestore db;
    private EditText eventName, maxAttendees, dateTime, eventDescription;
    private ImageView qrCodeImageView;

    private OnEventAddedListener eventAddedListener; // Listener for notifying when an event is added

    public void setOnEventAddedListener(OnEventAddedListener listener) {
        this.eventAddedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventName = view.findViewById(R.id.eventName);
        maxAttendees = view.findViewById(R.id.maxAttendees);
        dateTime = view.findViewById(R.id.dateTime);
        eventDescription = view.findViewById(R.id.eventDescription);
        qrCodeImageView = view.findViewById(R.id.eventQR);

        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Handle back button
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        // Handle save button
        view.findViewById(R.id.saveEventButton).setOnClickListener(v -> {
            String name = eventName.getText().toString().trim();
            String date = dateTime.getText().toString().trim();
            String description = eventDescription.getText().toString().trim();
            String maxAttendeesStr = maxAttendees.getText().toString().trim();

            // Validate input
            if (name.isEmpty() || date.isEmpty() || description.isEmpty() || maxAttendeesStr.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxAttendeesInt;
            try {
                maxAttendeesInt = Integer.parseInt(maxAttendeesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Max attendees must be a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new event object
            Event newEvent = new Event(name, date, organizerID, maxAttendeesInt, description);

            // Save to Firestore
            db.collection("events").add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        String eventId = documentReference.getId(); // Get unique ID
                        generateAndSaveQrCode(eventId); // Generate, display, and save QR code
                        Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                        if (eventAddedListener != null) {
                            eventAddedListener.onEventAdded(); // Notify listener
                        }
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void generateAndSaveQrCode(String eventId) {
        // Create a deep link to the event
        String eventLink = "https://yourapp.example.com/event/" + eventId;

        try {
            // Generate QR Code
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrCodeBitmap = barcodeEncoder.encodeBitmap(eventLink, BarcodeFormat.QR_CODE, 400, 400);

            // Display QR code in the ImageView
            qrCodeImageView.setImageBitmap(qrCodeBitmap);

            // Convert QR code to Base64
            String qrCodeBase64 = convertBitmapToBase64(qrCodeBitmap);

            // Save the Base64 string to Firestore
            db.collection("events").document(eventId)
                    .update("qrCode", qrCodeBase64)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "QR code saved to Firestore!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } catch (WriterException e) {
            Toast.makeText(getContext(), "Error generating QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Listener interface to notify when an event is added
    public interface OnEventAddedListener {
        void onEventAdded();
    }
}
