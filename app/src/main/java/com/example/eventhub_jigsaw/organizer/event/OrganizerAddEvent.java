package com.example.eventhub_jigsaw.organizer.event;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class OrganizerAddEvent extends DialogFragment {

    private FirebaseFirestore db;
    private EditText eventName, maxAttendees, dateTime, eventDescription;
    Spinner facilityLocation ;
    private ImageView qrCodeImageView;

    private OnEventAddedListener eventAddedListener;// Listener for notifying when an event is added

    // Facility list to hold facility data
    private ArrayList<Facility> facilitiesList = new ArrayList<>();

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
        facilityLocation  = view.findViewById(R.id.eventLocation);

        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch and populate the Spinner with facilities from Firestore
        loadFacilities();

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

            // Get selected facility name
            Facility selectedFacility = (Facility) facilityLocation.getSelectedItem();

            // Create a new event object
            Event newEvent = new Event(name, date, organizerID, maxAttendeesInt, description);
            newEvent.setWaitingList(new ArrayList<>()); // Initialize waiting list
            newEvent.setSampledUsers(new ArrayList<>()); // Initialize sampled users
            newEvent.setRegisteredUsers(new ArrayList<>());
            newEvent.setDeclinedInvitationUser(new ArrayList<>());

            // Save to Firestore
            db.collection("events").add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        String eventId = documentReference.getId(); // Get unique ID
                        generateAndSaveQrCode(eventId); // Generate QR code and save it
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            // Create Facility object (use the selected facility)
            Facility newFacility = selectedFacility;  // Selected facility
            // Populate the newFacility object based on the inputs, if required
    });
    }

    private void loadFacilities() {
        db.collection("facilities").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilitiesList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Facility facility = document.toObject(Facility.class);
                        if (facility != null) {
                            facilitiesList.add(facility);
                        }
                    }
                    // Update the spinner with the list of facility names
                    updateFacilitySpinner();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load facilities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void updateFacilitySpinner() {
        // Create a list of facility names
        ArrayList<String> facilityNames = new ArrayList<>();
        for (Facility facility : facilitiesList) {
            facilityNames.add(facility.getName());  // Assuming Facility class has a 'getName()' method
        }

        // Create an adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, facilityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilityLocation.setAdapter(adapter);
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
