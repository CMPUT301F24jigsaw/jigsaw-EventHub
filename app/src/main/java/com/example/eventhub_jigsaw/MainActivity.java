package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button UserLogin = findViewById(R.id.user_login);
        Button AdminLogin = findViewById(R.id.admin_login);

        AdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminSignIn.class);
            startActivity(intent);
        });

        UserLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserSignIn.class);
            startActivity(intent);
        });
    }
}