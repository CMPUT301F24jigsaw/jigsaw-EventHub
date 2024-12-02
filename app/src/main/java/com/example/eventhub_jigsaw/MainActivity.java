package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventhub_jigsaw.admin.AdminHomePage;
import com.example.eventhub_jigsaw.entrant.UserHomePage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * MainActivity is the entry point of the app, providing options for user and admin login.
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Firestore database instance
    private String userID; // Unique user identifier based on the device ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Link to the layout for this activity

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the unique device ID
        //userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.d("MainActivity", "Device ID: " + userID);

        // Initialize buttons for user and admin login
        Button userLogin = findViewById(R.id.user_login);
        Button adminLogin = findViewById(R.id.admin_login);

        // Handle Admin Login button click
        adminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminSignIn.class);
            startActivity(intent);
        });

        // Handle User Login button click
        userLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserSignUp.class);
            startActivity(intent);
        });
    }

    /**
     * Checks if the user exists in Firestore by userID.
     * Navigates to UserHomePage if the user exists, otherwise navigates to SignUpPage.
     *
     * @param userID The unique device ID of the user.
     */
    private void checkUserAndNavigate(String userID) {
        // Reference the user's document in the Firestore database
        DocumentReference docRef = db.collection("users").document(userID);

        // Fetch the document from Firestore
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // User exists, navigate to UserHomePage
                    Log.d("Firestore", "User exists: " + userID);
                    Intent intent = new Intent(MainActivity.this, UserHomePage.class);
                    intent.putExtra("userID", userID); // Pass userID to the UserHomePage
                    startActivity(intent);
                    finish(); // Close MainActivity
                } else {
                    // User does not exist, navigate to SignUpPage
                    Log.d("Firestore", "No user found for ID: " + userID);
                    Toast.makeText(this, "User not found. Redirecting to sign-up.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UserSignUp.class);
                    startActivity(intent);
                }
            } else {
                // Task failed, log the exception
                Log.e("Firestore", "Error fetching user", task.getException());
                Toast.makeText(this, "Error checking user. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
