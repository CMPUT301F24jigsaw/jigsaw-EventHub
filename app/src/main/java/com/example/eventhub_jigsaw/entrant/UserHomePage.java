package com.example.eventhub_jigsaw.entrant;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.MainActivity;
import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.entrant.UserMyprofile;
import com.example.eventhub_jigsaw.admin.ProfilesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserHomePage extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_homepage);

        bottomNavigationView = findViewById(R.id.bottomNavView_user);

        // Set the default fragment to display
        loadFragment(new UserEventPage());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.profile_user) {
                fragment = new UserMyprofile();
            } else if (item.getItemId() == R.id.home_user) {
                fragment = new UserEventPage();
            } else if (item.getItemId() == R.id.invites_user) {
                fragment = new UserInvitePageActivity();  // Create this fragment
            } else if (item.getItemId() == R.id.notifications_user) {
                fragment = new NotificationActivity(); // Create this fragment
            } else if (item.getItemId() == R.id.scan_qr_user) {
                fragment = new UserScanQR(); // Create this fragment
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
        fragmentTransaction.replace(R.id.flFragment_user, fragment);
        fragmentTransaction.commit();
    }
}
