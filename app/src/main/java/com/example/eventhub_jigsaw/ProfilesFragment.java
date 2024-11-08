package com.example.eventhub_jigsaw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
/**
 * ProfilesFragment is a fragment that displays a list of user profiles in a ListView.
 * When a profile is clicked, it navigates to the DeleteProfile fragment to handle profile deletion.
 */
public class ProfilesFragment extends Fragment {

    private ArrayList<Profiles> dataList;
    private ListView profileList;
    private ProfileArrayAdapter profileAdapter;
    /**
     * Called to create the view hierarchy for this fragment.
     *
     * @param inflater The LayoutInflater object to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should attach to.
     * @param savedInstanceState A Bundle with saved state data.
     * @return The root view of the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.profiles_page, container, false);

        // Sample profile data
        String[] usernames = {"ashwin"};
        String[] emails = {"ksndf"};

        // Initialize dataList with profile data
        dataList = new ArrayList<>();
        for (int i = 0; i < usernames.length; i++) {
            dataList.add(new Profiles(usernames[i], emails[i]));
        }

        // Set up ListView and adapter
        profileList = view.findViewById(R.id.profile_list);
        profileAdapter = new ProfileArrayAdapter(getContext(), dataList);
        profileList.setAdapter(profileAdapter);

        // Set item click listener to navigate to DeleteProfile fragment
        profileList.setOnItemClickListener((parent, v, position, id) -> {
            Profiles selectedProfile = dataList.get(position);

            // Create an instance of DeleteProfile fragment
            DeleteProfile deleteProfileFragment = new DeleteProfile();

            // Create a Bundle to pass the selected profile's data
            Bundle args = new Bundle();
            args.putString("username", selectedProfile.getUsername());
            args.putString("email", selectedProfile.getEmail());

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
}
