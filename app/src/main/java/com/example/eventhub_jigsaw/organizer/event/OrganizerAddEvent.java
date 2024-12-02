package com.example.eventhub_jigsaw.organizer.event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.SelectImage;
import com.example.eventhub_jigsaw.UploadImage;
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
    private EditText eventName, maxAttendees, dateTime, eventDescription, waitList;
    Spinner facilityLocation, userGeolocation ;
    private ImageView qrCodeImageView;
    private ImageView selectedImageView;
    private Button buttonUploadImage;
    private Uri selectedImageUri;
    private SelectImage selectImage;
    private UploadImage uploadImage;

    private OnEventAddedListener eventAddedListener;// Listener for notifying when an event is added

    // Facility list to hold facility data
    private ArrayList<Facility> facilityNames = new ArrayList<>();

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
        facilityLocation = view.findViewById(R.id.eventLocation);
        buttonUploadImage = view.findViewById(R.id.editImageButton);
        selectedImageView = view.findViewById(R.id.eventImage);
        selectImage = new SelectImage(activityResultLauncher, selectedImageView); // Initialize SelectImage helper
        uploadImage = new UploadImage(); // Initialize UploadImage helper
        userGeolocation = view.findViewById(R.id.eventGeoLocation);
        waitList = view.findViewById(R.id.limit_waitlist);

        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();

        loadFacilities();

        // Handle image selection
        buttonUploadImage.setOnClickListener(v -> {
            selectImage.selectImage(); // Trigger the image picker
        });


        view.findViewById(R.id.saveEventButton).setOnClickListener(v -> {
            if (!validateInput()) return;

            String name = eventName.getText().toString().trim();
            String date = dateTime.getText().toString().trim();
            String description = eventDescription.getText().toString().trim();
            int maxAttendeesInt = Integer.parseInt(maxAttendees.getText().toString().trim());

            Facility selectedFacility = (Facility) facilityLocation.getSelectedItem();
            boolean geolocation = userGeolocation.getSelectedItem().toString().equalsIgnoreCase("Yes");

            Event newEvent = new Event(null, name, date, organizerID, maxAttendeesInt, description);
            newEvent.setFacilityId(selectedFacility.getFacilityID());
            newEvent.setWaitingList(new ArrayList<>());
            newEvent.setSampledUsers(new ArrayList<>());
            newEvent.setRegisteredUsers(new ArrayList<>());
            newEvent.setDeclinedInvitationUser(new ArrayList<>());
            newEvent.setImageID(null);
            newEvent.setGeolocation(geolocation);

            String waitingListLimitStr = waitList.getText().toString().trim();
            if (!TextUtils.isEmpty(waitingListLimitStr)) {
                try {
                    int waitingListLimitInt = Integer.parseInt(waitingListLimitStr);
                    if (waitingListLimitInt > 0) {
                        newEvent.setWaitingListLimit(waitingListLimitInt);
                    }
                } catch (NumberFormatException e) {
                    showToast("Waiting list limit must be a valid number.");
                    return;
                }
            }

            // Add the event to the Firestore collection
            db.collection("events").add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        // Get the event ID from Firestore and generate the QR code
                        String eventId = documentReference.getId();
                        newEvent.setId(eventId);
                        generateAndSaveQrCode(eventId);

                        // Upload the image if an image is selected
                        if (selectedImageUri != null) {
                            uploadImage.uploadImageEvents(selectedImageUri, new UploadImage.OnUploadCompleteListener() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    // Store the image URL or handle the success logic
                                    db.collection("events").document(eventId)
                                            .update("imageID", imageUrl)
                                            .addOnSuccessListener(aVoid -> {
                                                // Optionally show a success message
                                                Log.e("OrganizerAddEvent", "Image uploaded: " + imageUrl);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("OrganizerAddEvent", "Image uploaded: " + imageUrl);
                                            } );
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    // Show a failure message for image upload
                                    showToast("Failed to upload image: " + errorMessage);
                                }
                            }, eventId);
                        } else {
                            showToast("Event created successfully without an image.");
                        }
                    })
                    .addOnFailureListener(e -> showToast("Failed to create event: " + e.getMessage()));
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                selectImage.getActivityResultCallback().onActivityResult(result);
                selectedImageUri = selectImage.getSelectedImageUri();
            }
    );
    private void loadFacilities() {
        db.collection("facilities").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> facilityIds = new ArrayList<>();

                    // Collect facility IDs
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        facilityIds.add(document.getId());
                    }

                    // Retrieve facility names based on their IDs
                    fetchFacilityNames(facilityIds);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load facilities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFacilityNames(List<String> facilityIds) {
        for (String facilityId : facilityIds) {
            db.collection("facilities").document(facilityId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String facilityName = documentSnapshot.getString("name");
                            if (facilityName != null) {
                                Facility facility = new Facility(facilityId, facilityName);
                                facilityNames.add(facility);
                            }
                            if (facilityNames.size() == facilityIds.size()) {
                                updateFacilitySpinner();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error fetching facility details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateFacilitySpinner() {
        ArrayAdapter<Facility> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, facilityNames);
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
            if (maxAttendeesInt <= 0) {
                showToast("Max attendees must be a positive number.");
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
