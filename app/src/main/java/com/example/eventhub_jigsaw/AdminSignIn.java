package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventhub_jigsaw.admin.AdminHomePage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * AdminSignIn allows administrators to log in by verifying their isAdmin status in Firestore.
 */
public class AdminSignIn extends AppCompatActivity {

    private FirebaseFirestore db; // Firestore database instance
    private EditText adminIdField; // Field to input admin ID (could be email or userID)
    private Button signInButton, backButton; // Button to initiate login
    private String adminID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_sign_in); // Link to the layout for this activity

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        signInButton = findViewById(R.id.sign_in_button);
        backButton = findViewById(R.id.button_back);

        // Handle the sign-in button click
        signInButton.setOnClickListener(v -> {
            adminID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


            checkAdminAndNavigate(adminID);

        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminSignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


    }

    /**
     * Checks if the given admin ID belongs to an admin and navigates to AdminHomePage if valid.
     *
     * @param adminID The unique identifier of the admin (e.g., userID or email).
     */
    private void checkAdminAndNavigate(String adminID) {
        // Reference the user's document in the Firestore database
        DocumentReference docRef = db.collection("users").document(adminID);

        // Fetch the document from Firestore
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Boolean isAdmin = document.getBoolean("admin");
                    if (isAdmin != null && isAdmin) {
                        // User is an admin, navigate to AdminHomePage
                        Log.d("Firestore", "Admin login successful: " + adminID);
                        Intent intent = new Intent(AdminSignIn.this, AdminHomePage.class);
                        intent.putExtra("adminID", adminID); // Pass adminID to the AdminHomePage
                        startActivity(intent);
                        finish(); // Close AdminSignIn
                    } else {
                        // User is not an admin
                        Log.d("Firestore", "Non-admin user attempted login: " + adminID);
                        Toast.makeText(this, "You do not have admin privileges.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User does not exist
                    Log.d("Firestore", "No user found for ID: " + adminID);
                    Toast.makeText(this, "Admin ID not found. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Task failed, log the exception
                Log.e("Firestore", "Error fetching admin user", task.getException());
                Toast.makeText(this, "Error checking admin. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

