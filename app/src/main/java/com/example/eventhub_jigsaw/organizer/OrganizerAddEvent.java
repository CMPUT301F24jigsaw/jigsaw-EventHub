package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrganizerAddEvent extends DialogFragment {

    private FirebaseFirestore db;
    private EditText eventName, maxAttendees, dateTime, eventDescription;

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

            // Validate input
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Event name is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(date)) {
                Toast.makeText(getContext(), "Event date is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(description)) {
                Toast.makeText(getContext(), "Event description is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(maxAttendeesStr)) {
                Toast.makeText(getContext(), "Max attendees is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Validate event description length
            if (description.length() < 10 || description.length() > 500) {
                Toast.makeText(getContext(), "Description must be between 10 and 500 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate date format (dd/mm/yyyy)
            if (!isValidDate(date)) {
                Toast.makeText(getContext(), "Invalid date format. Use dd/mm/yyyy.", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxAttendeesInt;
            try {
                maxAttendeesInt = Integer.parseInt(maxAttendeesStr);
                if (maxAttendeesInt <= 0 || maxAttendeesInt > 1000) {
                    Toast.makeText(getContext(), "Max attendees must be a positive number.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Max attendees must be a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new event object
            Event newEvent = new Event(name, date, organizerID, maxAttendeesInt, description);

            // Save to Firestore
            db.collection("events").add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                        if (eventAddedListener != null) {
                            eventAddedListener.onEventAdded(); // Notify listener
                        }
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    // Listener interface to notify when an event is added
    public interface OnEventAddedListener {
        void onEventAdded();
    }

    private boolean isValidDate(String date) {
        // Split the date string into day, month, and year
        String[] parts = date.split("/");

        // Check if the date has exactly three parts (day, month, year)
        if (parts.length != 3) {
            Toast.makeText(getContext(), "Invalid date format. Please use dd/mm/yyyy.", Toast.LENGTH_SHORT).show();
            return false;
        }

        int day, month, year;

        try {
            // Convert parts into integers
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);

            // Convert 2-digit year to 4-digit year (assumes 21st century for 2-digit year)
            if (year < 100) {
                year += 2000;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid date format. Please use dd/mm/yyyy.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get the current year
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        // Validate year (event should not be more than 3 years beyond the current year)
        if (year > currentYear + 3) {
            Toast.makeText(getContext(), "Event year must not be more than 3 years from now.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate month range (should be between 01 and 12)
        if (month < 1 || month > 12) {
            Toast.makeText(getContext(), "Month must be between 01 and 12.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate day range (should be between 01 and 31)
        if (day < 1 || day > 31) {
            Toast.makeText(getContext(), "Day must be between 01 and 31.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Handle month-day specific checks (e.g., February 30)
        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
            Toast.makeText(getContext(), "This month has only 30 days.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (month == 2) {
            // Handle leap year for February
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                if (day > 29) {
                    Toast.makeText(getContext(), "February in a leap year has only 29 days.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                if (day > 28) {
                    Toast.makeText(getContext(), "February has only 28 days in a non-leap year.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

}