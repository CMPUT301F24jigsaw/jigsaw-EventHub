package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
/**
 * FacilitiesFragment manages a list of facilities
 * Allows administrators to view, add, or remove facilities using DB.
 *
 * Outstanding Issues:
 * - No confirmation dialog for removing a facility.
 */

public class FacilitiesFragment extends Fragment {

    private FacilityArrayAdapter facilityArrayAdapter;
    private ArrayList<Facility> facilityDataList;
    private ListView facilityList;
    private Button removeFacilityButton;
    int lastClickedPosition = -1;

    // Database
    FirebaseFirestore db;
    CollectionReference facilitiesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facilities_list, container, false);

        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");

        facilityList = view.findViewById(R.id.facility_list);
        facilityDataList = new ArrayList<>();
        facilityArrayAdapter = new FacilityArrayAdapter(requireContext(), facilityDataList);
        facilityList.setAdapter(facilityArrayAdapter);

        removeFacilityButton = view.findViewById(R.id.remove_facility_btn);

        // create snapshot listener to update database live
        facilitiesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    facilityDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        try {
                            String organizerId = doc.getString("organizerID");
                            String name = doc.getString("name");
                            String location = doc.getString("location");
                            int capacity = doc.get("capacity", int.class);
                            Log.d("Firestore", String.format("Facility(%s, %s) fetched", name, location));
                            facilityDataList.add(new Facility(name, organizerId, location, capacity));
                        } catch (Exception e) {
                            Log.e("Firestore", "Error processing document: " + doc.getId(), e);
                        }
                    }
                    facilityArrayAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "No facilities found");
                }
            }
        });

        // add listener for listview items
        facilityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastClickedPosition = i; // record item position
            }
        });

        removeFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickedPosition != -1) {
                    Facility facility = facilityDataList.remove(lastClickedPosition);
                    deleteFacility(facility);
                }
            }
        });


        return view;
    }

    private void deleteFacility(Facility facility) {
        // reset lastClickedPosition
        lastClickedPosition = -1;
        // remove city from local list
        facilityDataList.remove(facility);
        facilityArrayAdapter.notifyDataSetChanged();

        // Remove city from Firestore collection with city name as the document Id
        facilitiesRef.document(facility.getFacilityID()).delete();
    }
}
