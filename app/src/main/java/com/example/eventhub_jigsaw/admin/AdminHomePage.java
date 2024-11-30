package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This class represents the Admin Home page, which extends
 * It manages the bottom navigation functionality, allowing the user to switch between
 * different fragments like Profiles, Events, and Facilities.
 */
public class AdminHomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView; // enables Fragment switching

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_homepage);


        bottomNavigationView = findViewById(R.id.bottomNavView);

        // Set the default fragment to display
        loadFragment(new ProfilesFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.profile) {
                fragment = new ProfilesFragment();
            } else if (item.getItemId() == R.id.events) {
                fragment = new EventActivity(); // Create this fragment
            } else if (item.getItemId() == R.id.facilities) {
                fragment = new ProfilesFragment(); // Create this fragment
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();
    }
}
