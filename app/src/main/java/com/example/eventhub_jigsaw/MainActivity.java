package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/**
 * MainActivity is the entry point of the app, displaying the main screen with options
 * for user and admin login. It also manages edge-to-edge UI configurations.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created. Sets up the main layout,
     * configures edge-to-edge display, and initializes buttons for navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Adjust padding to account for system bars.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements.
        Button UserLogin = findViewById(R.id.user_login);
        Button AdminLogin = findViewById(R.id.admin_login);

        // Set up click listener for Admin Login button to navigate to AdminSignIn activity.
        AdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminSignIn.class);
            startActivity(intent);
        });
    }
}