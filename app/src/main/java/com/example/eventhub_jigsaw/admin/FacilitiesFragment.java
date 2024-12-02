package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * FacilitiesFragment manages a list of facilities.
 */
public class FacilitiesFragment extends Fragment {

    private FacilityArrayAdapter facilityArrayAdapter;
    private ArrayList<Facility> facilityDataList;
    private ListView facilityList;
    private Button removeFacilityButton;
    private int lastClickedPosition = -1;

    // Firebase Firestore references
    FirebaseFirestore db;
    CollectionReference facilitiesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facilities_list, container, false);

        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");

        facilityList = view.findViewById(R.id.facility_list);
        removeFacilityButton = view.findViewById(R.id.remove_facility_btn);

        facilityDataList = new ArrayList<>();
        facilityArrayAdapter = new FacilityArrayAdapter(requireContext(), facilityDataList);
        facilityList.setAdapter(facilityArrayAdapter);

        loadFacilities();

        facilityList.setOnItemClickListener((adapterView, view1, position, id) -> {
            lastClickedPosition = position;
            Log.d("FacilitiesFragment", "Item clicked at position: " + position);
        });

        removeFacilityButton.setOnClickListener(v -> {
            if (lastClickedPosition != -1) {
                Facility facility = facilityDataList.get(lastClickedPosition);
                confirmDelete(facility);
            } else {
                Toast.makeText(getContext(), "Please select a facility to delete", Toast.LENGTH_SHORT).show();
                Log.d("FacilitiesFragment", "No item selected for removal.");
            }
        });

        return view;
    }

    private void loadFacilities() {
        facilitiesRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error fetching facilities", error);
                return;
            }
            if (querySnapshots != null) {
                facilityDataList.clear();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    try {
                        String organizerId = doc.getString("organizerID");
                        String name = doc.getString("name");
                        String location = doc.getString("location");
                        int capacity = doc.contains("capacity") ? doc.get("capacity", int.class) : 0;

                        facilityDataList.add(new Facility(name, doc.getId(), location, capacity));
                        Log.d("Firestore", "Facility added: " + name);
                    } catch (Exception e) {
                        Log.e("Firestore", "Error processing document: " + doc.getId(), e);
                    }
                }
                facilityArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("Firestore", "No facilities found");
            }
        });
    }

    private void confirmDelete(Facility facility) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Facility")
                .setMessage("Are you sure you want to delete this facility?")
                .setPositiveButton("Yes", (dialog, which) -> deleteFacility(facility))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteFacility(Facility facility) {
        String facilityName = facility.getName();
        String facilityLocation = facility.getLocation();
        int facilityCapacity = facility.getCapacity();

        facilitiesRef
                .whereEqualTo("name", facilityName)
                .whereEqualTo("location", facilityLocation)
                .whereEqualTo("capacity", facilityCapacity)
                .get()
                .addOnSuccessListener(DocumentSnapshots -> {
                    if (!DocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : DocumentSnapshots.getDocuments()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FacilitiesFragment", "Facility deleted successfully: " + facilityName);
                                        facilityDataList.remove(facility);
                                        facilityArrayAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> Log.e("FacilitiesFragment", "Failed to delete facility: " + e.getMessage()));
                        }
                    } else {
                        Log.e("FacilitiesFragment", "No matching facility found for deletion.");
                        Toast.makeText(getContext(), "No matching facility found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FacilitiesFragment", "Error querying facilities: " + e.getMessage());
                    Toast.makeText(getContext(), "Error deleting facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
