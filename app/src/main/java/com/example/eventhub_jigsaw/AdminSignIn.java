package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminSignIn extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables edge-to-edge display
        setContentView(R.layout.admin_signin);

        Button AdminSign = findViewById(R.id.admin_sign);

        AdminSign.setOnClickListener(v -> {
            Intent intent = new Intent(AdminSignIn.this, AdminHomePage.class);
            startActivity(intent);
        });

    }
}
