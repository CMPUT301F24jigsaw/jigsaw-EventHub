package com.example.eventhub_jigsaw.organizer.facility;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganizerFacilityInfo extends DialogFragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_facility_info, container, false);

        db = FirebaseFirestore.getInstance();

        // Get the arguments passed to this dialog
        Bundle args = getArguments();
        String facilityName = args != null ? args.getString("event_name", "Unknown Facility") : "Unknown Facility";
        String facilityLocation = args != null ? args.getString("event_location", "Unknown Location") : "Unknown Location";
        int facilityCapacity = args != null ? args.getInt("event_capacity", 0) : 0;

        // Initialize TextViews
        TextView nameTextView = view.findViewById(R.id.facility_name_text);
        TextView locationTextView = view.findViewById(R.id.facility_location_text);
        TextView capacityTextView = view.findViewById(R.id.facility_capacity_text);

        // Set values to the TextViews
        nameTextView.setText(facilityName);
        locationTextView.setText(facilityLocation);
        capacityTextView.setText(String.valueOf(facilityCapacity));

        // Set up the close button
        view.findViewById(R.id.close_button).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.delete_button).setOnClickListener(v -> {
            db.collection("facilities")
                    .whereEqualTo("name", facilityName)
                    .whereEqualTo("location", facilityLocation)
                    .whereEqualTo("capacity", facilityCapacity)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Delete all matching documents
                            for (var document : queryDocumentSnapshots.getDocuments()) {
                                document.getReference().delete()
                                        .addOnSuccessListener(aVoid ->
                                                Log.e("OrganizerFacilityInfo", "Facility deleted successfully!")
                                        )
                                        .addOnFailureListener(e ->
                                                Log.e("OrganizerFacilityInfo", "Failed to delete facility: " + e.getMessage())
                                        );
                            }
                            dismiss();
                        } else {
                            Log.e("OrganizerFacilityInfo", "No Facility Found!");
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.e("OrganizerFacilityInfo", "Error querying facilities: " + e.getMessage())
                    );
        });


        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Make the dialog full-screen
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
}
