package com.example.eventhub_jigsaw.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
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
import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class OrganizerAddEvent extends DialogFragment {

    private FirebaseFirestore db;
    private EditText eventName, maxAttendees, dateTime, eventDescription, facilityName, facilityLocation;
    private ImageView qrCodeImageView;

    private OnEventAddedListener eventAddedListener;// Listener for notifying when an event is added

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
        // needs to be reflected in the corresponding xml file
        facilityName = view.findViewById(R.id.eventLocation);
        facilityLocation = view.findViewById(R.id.facilityLocation);

        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Handle back button
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        // Handle save button
        view.findViewById(R.id.saveEventButton).setOnClickListener(v -> {
            if (!validateInput()) {
                return; // Stop if validation fails
            }

            String name = eventName.getText().toString().trim();
            String date = dateTime.getText().toString().trim();
            String description = eventDescription.getText().toString().trim();
            int maxAttendeesInt = Integer.parseInt(maxAttendees.getText().toString().trim());
            String facilityname = facilityName.getText().toString().trim();
            String facilitylocation = facilityLocation.getText().toString().trim();

            // Create a new event object
            Event newEvent = new Event(name, date, organizerID, maxAttendeesInt, description);
            newEvent.setWaitingList(new ArrayList<>()); // Initialize waiting list
            newEvent.setSampledUsers(new ArrayList<>()); // Initialize sampled users
            newEvent.setRegisteredUsers(new ArrayList<>());

            //Create a new facility
            Facility newFacility = new Facility(organizerID, facilityname, facilitylocation, maxAttendeesInt);

            // Save to Firestore
            db.collection("events").add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        String eventId = documentReference.getId(); // Get unique ID
                        generateAndSaveQrCode(eventId); // Generate QR code and save it
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            db.collection("facility").add(newFacility)
                    .addOnSuccessListener(documentReference -> {
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to create facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void generateAndSaveQrCode(String eventId) {
        String eventLink = "https://yourapp.example.com/event/" + eventId;

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrCodeBitmap = barcodeEncoder.encodeBitmap(eventLink, BarcodeFormat.QR_CODE, 400, 400);

            // Display QR code in the ImageView
            qrCodeImageView.setImageBitmap(qrCodeBitmap);

            // Convert QR code to Base64
            String qrCodeBase64 = convertBitmapToBase64(qrCodeBitmap);

            // Save the Base64 string to Firestore
            db.collection("events").document(eventId)
                    .update("qrCode", qrCodeBase64)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "QR code saved to Firestore!", Toast.LENGTH_SHORT).show();
                        if (eventAddedListener != null) {
                            eventAddedListener.onEventAdded(); // Notify listener
                        }
                        dismiss(); // Close the fragment after success
                    })
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

    private boolean validateInput() {
        String name = eventName.getText().toString().trim();
        String date = dateTime.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String maxAttendeesStr = maxAttendees.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            showToast("Event name is required.");
            return false;
        }
        if (TextUtils.isEmpty(date)) {
            showToast("Event date is required.");
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            showToast("Event description is required.");
            return false;
        }
        if (description.length() < 10 || description.length() > 500) {
            showToast("Description must be between 10 and 500 characters.");
            return false;
        }
        if (!isValidDate(date)) {
            showToast("Invalid date format. Use dd/mm/yyyy.");
            return false;
        }
        if (TextUtils.isEmpty(maxAttendeesStr)) {
            showToast("Max attendees is required.");
            return false;
        }
        try {
            int maxAttendeesInt = Integer.parseInt(maxAttendeesStr);
            if (maxAttendeesInt <= 0 || maxAttendeesInt > 1000) {
                showToast("Max attendees must be a positive number between 1 and 1000.");
                return false;
            }
        } catch (NumberFormatException e) {
            showToast("Max attendees must be a valid number.");
            return false;
        }
        return true;
    }

    private boolean isValidDate(String date) {
        Pattern datePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
        if (!datePattern.matcher(date).matches()) {
            return false;
        }

        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (year < Calendar.getInstance().get(Calendar.YEAR) || year > Calendar.getInstance().get(Calendar.YEAR) + 3) {
            return false;
        }
        if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if (month == 2) {
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            if (isLeapYear && day > 29 || !isLeapYear && day > 28) {
                return false;
            }
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnEventAddedListener {
        void onEventAdded();
    }
}
