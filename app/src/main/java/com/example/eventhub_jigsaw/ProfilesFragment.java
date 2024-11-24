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

public class ProfilesFragment extends Fragment {

    private ArrayList<com.example.eventhub_jigsaw.Profiles> dataList;
    private ListView profileList;
    private com.example.eventhub_jigsaw.ProfileArrayAdapter profileAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profiles_page, container, false);

        String[] usernames = {"ashwin"};
        String[] emails = {"ksndf"};

        dataList = new ArrayList<>();
        for (int i = 0; i < usernames.length; i++) {
            dataList.add(new com.example.eventhub_jigsaw.Profiles(usernames[i], emails[i]));
        }

        profileList = view.findViewById(R.id.profile_list);
        profileAdapter = new com.example.eventhub_jigsaw.ProfileArrayAdapter(getContext(), dataList);
        profileList.setAdapter(profileAdapter);

        profileList.setOnItemClickListener((parent, v, position, id) -> {
            com.example.eventhub_jigsaw.Profiles selectedProfile = dataList.get(position);

            // Create an instance of DeleteProfile fragment
            com.example.eventhub_jigsaw.DeleteProfile deleteProfileFragment = new com.example.eventhub_jigsaw.DeleteProfile();

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
