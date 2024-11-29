package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class OrganizerAddFacility extends DialogFragment {

    private FirebaseFirestore db;
    private EditText facilityName, facilityLocation, facilityCapacity;

    private OnFacilityAddedListener facilityAddedListener;

    public void setOnFacilityAddedListener(OnFacilityAddedListener listener){
        this.facilityAddedListener = listener;
    }

    public interface OnFacilityAddedListener{
        void OnFacilityAdded();
    }

    //On create view

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_add_facility, container, false);
    }


    //On view created
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        facilityName = view.findViewById(R.id.facilityName);
        facilityLocation = view.findViewById(R.id.facilityLocation);
        facilityCapacity = view.findViewById(R.id.facilityCapacity);

        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Handle back button
        ImageButton backButton = view.findViewById(R.id.facilityBackButton);
        backButton.setOnClickListener(v -> dismiss());

        //Handle Save Button
        view.findViewById(R.id.saveFacilityButton).setOnClickListener(v -> {
            String name = facilityName.getText().toString().trim();
            String location = facilityLocation.getText().toString().trim();
            String capacity = facilityCapacity.getText().toString().trim();

            //Validate input
            if (name.isEmpty() || location.isEmpty() || capacity.isEmpty()){
                Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Event name is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(location)) {
                Toast.makeText(getContext(), "Event location is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(capacity)) {
                Toast.makeText(getContext(), "Event capacity is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacityInt;
            try {
                capacityInt = Integer.parseInt(capacity);
                if (capacityInt <= 0 || capacityInt > 1000) {
                    Toast.makeText(getContext(), "Capacity must be a positive number.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Capacity must be a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Create a new facility object

            Facility newFacility = new Facility(name, location, capacityInt);


            //Save to Firebase

            db.collection("facility").add(newFacility)
                    .addOnSuccessListener(documentReference -> {
                        String facilityID = documentReference.getId(); //get unique ID
                        Toast.makeText(getContext(), "Facility created successfully!", Toast.LENGTH_SHORT).show();
                        if (facilityAddedListener != null){
                            facilityAddedListener.OnFacilityAdded(); //Notify Listener
                        }
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

    }


}

