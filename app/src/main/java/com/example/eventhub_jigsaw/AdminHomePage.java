package com.example.eventhub_jigsaw;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

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
