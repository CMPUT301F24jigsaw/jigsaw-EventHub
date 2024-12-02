package com.example.eventhub_jigsaw.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.MainActivity;
import com.example.eventhub_jigsaw.admin.ProfilesFragment;
import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.entrant.UserScanQR;
import com.example.eventhub_jigsaw.organizer.event.OrganizerEventActivity;
import com.example.eventhub_jigsaw.organizer.facility.OrganizerFacilityActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
/**
 * OrganizerHomePage is the main activity for the organizer's interface.
 * Handles the bottom navigation to switch between various fragments.
 */

public class OrganizerHomePage extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    /**
     * Initializes the activity and sets up the bottom navigation listener.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_homepage);

        bottomNavigationView = findViewById(R.id.bottomNavView_organizer);

        // Set the default fragment to display (optional)
        loadFragment(new OrganizerEventActivity()); // TEMPORARY, replace with actual default fragment

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.profile_organizer) {
                fragment = new OrganizerMyProfile(); // TEMPORARILY HARDCODED
            } else if (item.getItemId() == R.id.home_organizer) {
                fragment = new OrganizerEventActivity();
            } else if (item.getItemId() == R.id.facilities_organizer) {
                fragment = new OrganizerFacilityActivity();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });

    }

    /**
     * Replaces the current fragment with the given fragment.
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Ensure the transaction is a replacement
        fragmentTransaction.replace(R.id.flFragment_organizer, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: If you want the fragment to be added to the back stack
        fragmentTransaction.commit();
    }

}