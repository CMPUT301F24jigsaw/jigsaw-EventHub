package com.example.eventhub_jigsaw.organizer.facility;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerFacilityActivity extends Fragment {

    private List<OrganizerFacilityPage> facilityList;
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

        // Fetch initial facilities
        fetchFacilitiesByOrganizer();

        // Add facility button logic
        FloatingActionButton addFacilityButton = view.findViewById(R.id.addFacility);
        addFacilityButton.setOnClickListener(v -> {
            OrganizerAddFacility addFacilityDialog = new OrganizerAddFacility();
            addFacilityDialog.setOnEventAddedListener(this::fetchFacilitiesByOrganizer); // Refresh events after adding
            addFacilityDialog.show(getChildFragmentManager(), "AddEventDialog");
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
                        Toast.makeText(getContext(), "Error fetching facilities: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String facilityName = document.getString("facilityName");

                            facilityList.add(new OrganizerFacilityPage(facilityName));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
