package com.example.eventhub_jigsaw.organizer.facility;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerFacilityActivity extends Fragment {

    private static final String TAG = "OrganizerFacilityActivity";
    private List<Facility> facilityList;
    private OrganizerFacilityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizers_facilities_page, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFacilities_organizer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        facilityList = new ArrayList<>();
        adapter = new OrganizerFacilityAdapter(facilityList, getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        // Fetch facilities
        fetchFacilitiesByOrganizer();

        // Add facility button logic
        FloatingActionButton addFacilityButton = view.findViewById(R.id.addFacility);
        addFacilityButton.setOnClickListener(v -> {
            OrganizerAddFacility addFacilityDialog = new OrganizerAddFacility();
            addFacilityDialog.setOnEventAddedListener(this::fetchFacilitiesByOrganizer); // Refresh facilities after adding
            addFacilityDialog.show(getChildFragmentManager(), "AddFacilityDialog");
        });

        return view;
    }

    private void fetchFacilitiesByOrganizer() {
        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("facilities")
                .whereEqualTo("organizerID", organizerID)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error fetching facilities: " + error.getMessage(), error);
                        Toast.makeText(getContext(), "Error fetching facilities: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            try {
                                String facilityName = document.getString("name");
                                String facilityID = document.getId();
                                Long capacityLong = document.getLong("capacity");
                                int facilityCapacity = capacityLong != null ? capacityLong.intValue() : 0;
                                String facilityLocation = document.getString("location");

                                Facility facility = new Facility(facilityID, facilityName);
                                facility.setLocation(facilityLocation);
                                facility.setCapacity(facilityCapacity);

                                if (facility.getName() != null && facility.getLocation() != null) {
                                    facilityList.add(facility);
                                } else {
                                    Log.w(TAG, "Invalid facility data: " + document.getId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing facility data: " + document.getId(), e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
