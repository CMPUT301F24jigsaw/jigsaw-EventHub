package com.example.eventhub_jigsaw.organizer.facility;

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

public class OrganizerAddFacility extends DialogFragment {

    private FirebaseFirestore db;
    private EditText facilityName, maxAttendees, facilityLocation;
    private OnEventAddedListener facilityAddedListener; // Listener for notifying when a facility is added

    public void setOnEventAddedListener(OnEventAddedListener listener) {
        this.facilityAddedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizers_add_facility, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facilityName = view.findViewById(R.id.facility_name);
        maxAttendees = view.findViewById(R.id.maxAttendees);
        facilityLocation = view.findViewById(R.id.facilityLocation);

        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Handle back button
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        // Handle save button
        view.findViewById(R.id.saveFacilityButton).setOnClickListener(v -> {
            if (!validateInput()) {
                return; // Stop if validation fails
            }

            String name = facilityName.getText().toString().trim();
            int maxAttendeesInt = Integer.parseInt(maxAttendees.getText().toString().trim());
            String facilityLocationStr = facilityLocation.getText().toString().trim();  // Corrected line here

            // Create a new facility object
            Facility newFacility = new Facility(name, organizerID, facilityLocationStr, maxAttendeesInt);

            // Add the facility to Firestore within a try-catch block
            try {
                db.collection("facilities").add(newFacility)
                        .addOnSuccessListener(documentReference -> {
                            String facilityId = documentReference.getId();  // Get the Firestore-generated ID
                            newFacility.setId(facilityId);  // Set the facility ID on the object
                            showToast("Facility added successfully!");

                            // Optionally, notify the listener
                            if (facilityAddedListener != null) {
                                facilityAddedListener.onEventAdded();
                            }
                            dismiss();
                        })
                        .addOnFailureListener(e -> {
                            showToast("Failed to create facility: " + e.getMessage());
                        });
            } catch (Exception e) {
                // Handle any unexpected errors
                showToast("An unexpected error occurred: " + e.getMessage());
            }
        });
    }


    private boolean validateInput() {
        String name = facilityName.getText().toString().trim();
        String maxAttendeesStr = maxAttendees.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            showToast("Facility name is required.");
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

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnEventAddedListener {
        void onEventAdded();
    }
}

