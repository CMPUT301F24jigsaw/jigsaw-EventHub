package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_homepage);

        bottomNavigationView = findViewById(R.id.bottomNavView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            if (item.getItemId() == R.id.profile) {
                intent = new Intent(this, AdminSignIn.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.events) {
                intent = new Intent(this, AdminSignIn.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.facilities) {
                intent = new Intent(this, AdminSignIn.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

    }
}
