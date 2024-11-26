package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrganizerHomePage extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_homepage);

        bottomNavigationView = findViewById(R.id.bottomNavView_organizer);

        // Set the default fragment to display (optional)
        loadFragment(new EventActivity()); // TEMPORARY, replace with actual default fragment

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.profile_organizer) {
                fragment = new UserMyprofile(); // TEMPORARILY HARDCODED
            } else if (item.getItemId() == R.id.home_organizer) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.invites_organizer) {
                fragment = new OrganizerEventActivity();
            } else if (item.getItemId() == R.id.notifications_organizer) {
                fragment = new ProfilesFragment();
            } else if (item.getItemId() == R.id.scan_qr_organizer) {
                fragment = new UserScanQR();
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
        fragmentTransaction.replace(R.id.flFragment_organizer, fragment);
        fragmentTransaction.commit();
    }
}
