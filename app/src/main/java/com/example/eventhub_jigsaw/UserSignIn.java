package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserSignIn extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signin);

        Button UserSign = findViewById(R.id.user_sign);
        Button Organizer = findViewById(R.id.organizer_sign);

        UserSign.setOnClickListener(v -> {
            Intent intent = new Intent(UserSignIn.this, UserHomePage.class);
            startActivity(intent);
        });
        Organizer.setOnClickListener(v -> {
            Intent intent = new Intent(UserSignIn.this, OrganizerHomePage.class);
            startActivity(intent);
        });
    }
}
