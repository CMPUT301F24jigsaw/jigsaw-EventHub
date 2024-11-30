package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfilesFragment extends Fragment {

    private ArrayList<Profiles> dataList;
    private ArrayList<Profiles> dataListcpy;
    private ArrayList<String> usernameList; //Store only usernames for display
    private ListView profileList;
    private ProfileArrayAdapter profileAdapter;
    private ArrayAdapter<String> usernameAdapter;

    private FirebaseFirestore db;

    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profiles_page, container, false);

        db = FirebaseFirestore.getInstance();

        dataList = new ArrayList<>();
        dataListcpy = new ArrayList<>();

        usernameList = new ArrayList<>();
        usernameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, usernameList);
        profileList = view.findViewById(R.id.profile_list);

        profileAdapter = new ProfileArrayAdapter(getContext(), dataList);
        profileList.setAdapter(usernameAdapter);

        searchView = view.findViewById(R.id.SearchBar);


        // Fetch user data from Firestore
        fetchUserData();

        // Set up the SearchView query listener to filter as text changes
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Optional: Behavior when search is submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText); // Filter data as query changes
                return false;
            }
        });


        profileList.setOnItemClickListener((parent, v, position, id) -> {
            Profiles selectedProfile = dataList.get(position);

            // Create an instance of DeleteProfile fragment
            DeleteProfile deleteProfileFragment = new DeleteProfile();

            // Create a Bundle to pass the selected profile's data
            Bundle args = new Bundle();
            args.putString("username", selectedProfile.getUsername());
            args.putString("email", selectedProfile.getEmail());
            args.putInt("position", position);

            // Set the arguments to the fragment
            deleteProfileFragment.setArguments(args);

            // Navigate to the DeleteProfile fragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flFragment, deleteProfileFragment);
            transaction.addToBackStack(null); // Allow back navigation
            transaction.commit();
        });

        return view;
    }

    // Fetch data from the users collection
    private void fetchUserData(){
        CollectionReference usersRef = db.collection("users");
        usersRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    populateDataList(querySnapshot);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                });
    }

    // Populate the lists with associated segments of the data
    private void populateDataList(QuerySnapshot querySnapshot){
        dataList.clear();
        usernameList.clear();
        querySnapshot.forEach(document -> {
            String username = document.getString("name");
            String email = document.getString("email");
            dataList.add(new Profiles(username, email));
            // dataListcpy to be used in SearchView later
            dataListcpy.add(new Profiles(username, email));
            usernameList.add(username);
        });
        // Notify the adapters of the data change
        usernameAdapter.notifyDataSetChanged();
        profileAdapter.notifyDataSetChanged();
    }

    // Method to filter data based on the search query
    private void filterData(String query) {

        if (query.isEmpty()){
            // revert to original and unfiltered lists
            usernameList.clear();
            dataList.clear();

            // Add all usernames and profiles back to the lists
            for (Profiles profile : dataListcpy) {
                usernameList.add(profile.getUsername());
                dataList.add(profile);
            }
        } else {
            // Filter the data based on the query
            ArrayList<String> filteredUsernames = new ArrayList<>();
            ArrayList<Profiles> filteredProfiles = new ArrayList<>();  // To store profiles that match the query

            // Loop through all items and filter based on the query
            for (int i = 0; i < usernameList.size(); i++) {
                String username = usernameList.get(i);
                Profiles profile = dataList.get(i); // Get the corresponding profile for this username

                // Check if the username contains the query string (case-insensitive)
                if (username.toLowerCase().contains(query.toLowerCase())) {
                    filteredUsernames.add(username);
                    filteredProfiles.add(profile);  // Add the matching profile to the filtered list
                }
            }

            // Update the ListView with the filtered data
            usernameList.clear();
            dataList.clear();  // Clear the full data list

            // Add the filtered results to both lists
            usernameList.addAll(filteredUsernames);
            dataList.addAll(filteredProfiles);

        }


        // Notify the adapters to update the ListView
        usernameAdapter.notifyDataSetChanged();
        profileAdapter.notifyDataSetChanged();
    }



}
