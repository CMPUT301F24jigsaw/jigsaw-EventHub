package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventhub_jigsaw.entrant.UserHomePage;
import com.example.eventhub_jigsaw.organizer.OrganizerHomePage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSignUp extends AppCompatActivity {

    TextView TextName, TextEmail, TextPhone;
    EditText EditName, EditEmail, EditPhone;
    Button ButtonSignUp;
    Spinner SpinnerUserType;
    private ImageView selectedImageView;
    private Button buttonUploadImage;
    private Uri selectedImageUri;
    private SelectImage selectImage;
    private UploadImage uploadImage;

    String userID; // Unique userID for the device
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore and retrieve userID
        db = FirebaseFirestore.getInstance();
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize image selection and upload components
        selectImage = new SelectImage(activityResultLauncher, selectedImageView);
        uploadImage = new UploadImage();

        // Check if the user already exists in Firestore
        checkUserExists(userID);
    }

    private void checkUserExists(String userID) {
        DocumentReference docRef = db.collection("users").document(userID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve the role from Firestore
                    String role = document.getString("role"); // Firestore stores enums as strings
                    if (role != null) {
                        Log.d("UserSignUp", "User exists with role: " + role);
                        if (role.equalsIgnoreCase("ENTRANT")) {
                            // Navigate to UserHomePage
                            navigateToHomePage();
                        } else if (role.equalsIgnoreCase("ORGANIZER")) {
                            // Navigate to OrganizerHomePage
                            navigateToOrganizerHomePage();
                        } else {
                            Log.e("UserSignUp", "Unknown role: " + role);
                            Toast.makeText(this, "Unknown user role. Please contact support.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("UserSignUp", "Role is missing for user: " + userID);
                        Toast.makeText(this, "User role is missing. Please contact support.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User does not exist, show the sign-up UI
                    Log.d("UserSignUp", "No user found for ID: " + userID);
                    setUpSignUpUI();
                }
            } else {
                // Firestore task failed
                Log.e("UserSignUp", "Error checking user", task.getException());
                Toast.makeText(this, "Error checking user. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setUpSignUpUI() {
        setContentView(R.layout.user_signin);

        // Initialize UI elements
        TextName = findViewById(R.id.text_name);
        TextEmail = findViewById(R.id.text_email);
        TextPhone = findViewById(R.id.text_phone);

        EditName = findViewById(R.id.edit_name);
        EditEmail = findViewById(R.id.edit_email);
        EditPhone = findViewById(R.id.edit_phone);
        SpinnerUserType = findViewById(R.id.spinner_user); // Spinner for user role

        ButtonSignUp = findViewById(R.id.button_signup);

        selectedImageView = findViewById(R.id.image_profile);
        selectImage = new SelectImage(activityResultLauncher, selectedImageView);  // Pass initialized ImageView
        buttonUploadImage = findViewById(R.id.button_upload_image);

        // Set up Spinner with role options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_type, // Array defined in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerUserType.setAdapter(adapter);

        // Set up click listener for Sign Up button
        ButtonSignUp.setOnClickListener(v -> registerUser());

        // Set up click listener for the image selection button
        buttonUploadImage.setOnClickListener(v -> selectImage.selectImage());
    }

    // ActivityResultLauncher setup for handling result from gallery
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                selectImage.getActivityResultCallback().onActivityResult(result);
                selectedImageUri = selectImage.getSelectedImageUri();
            }
    );

    private void navigateToHomePage() {
        Intent intent = new Intent(UserSignUp.this, UserHomePage.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void navigateToOrganizerHomePage() {
        Intent intent = new Intent(UserSignUp.this, OrganizerHomePage.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void registerUser() {
        String name = EditName.getText().toString().trim();
        String email = EditEmail.getText().toString().trim();
        String phoneStr = EditPhone.getText().toString().trim();
        String role = SpinnerUserType.getSelectedItem().toString(); // Get selected role

        // Input validation
        if (TextUtils.isEmpty(name)) {
            EditName.setError("Name is required!");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            EditEmail.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(phoneStr)) {
            EditPhone.setError("Phone number is required!");
            return;
        }
        if (TextUtils.isEmpty(role)) {
            Toast.makeText(this, "Please select a role!", Toast.LENGTH_SHORT).show();
            return;
        }

        int phone;
        try {
            phone = Integer.parseInt(phoneStr);
        } catch (NumberFormatException e) {
            EditPhone.setError("Invalid phone number!");
            return;
        }

        // Check if an image was selected
        if (selectedImageUri != null) {
            // If an image was selected, upload it
            uploadImage.uploadImage(selectedImageUri, new UploadImage.OnUploadCompleteListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Image uploaded successfully, save the URL to Firestore or use it in the user object
                    saveUserToFirestore(userID, name, email, phone, role, imageUrl);
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle failure in image upload
                    Toast.makeText(UserSignUp.this, "Failed to upload image: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }, userID);
        } else {
            // No image selected, proceed with saving the user to Firestore without an image
            saveUserToFirestore(userID, name, email, phone, role, null); // Pass null for imageUrl if no image was selected
        }
    }


    private void saveUserToFirestore(String userID, String name, String email, int phone, String role, String imageUrl) {
        // Convert role from String to Role enum
        User.Role userRole;
        try {
            userRole = User.Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid role provided.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create User object and ensure WaitingList is explicitly set
        User user = new User(name, email, userID, phone, userRole);
        user.setProfileImageUrl(imageUrl);  // Set the image URL
        user.setWaitingList(new ArrayList<>()); // Explicitly set an empty list
        user.setEventAcceptedByOrganizer(new ArrayList<>());
        user.setRegisteredEvents(new ArrayList<>());
        user.setNotifications(new ArrayList<>());

        // Save the User object to Firestore
        db.collection("users").document(userID)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                        clearFields();

                        if (role.equalsIgnoreCase("ENTRANT")) {
                            navigateToHomePage();
                        } else if (role.equalsIgnoreCase("ORGANIZER")) {
                            navigateToOrganizerHomePage();
                        } else {
                            Toast.makeText(this, "Unknown role. Please contact support.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to save user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearFields() {
        EditName.setText("");
        EditEmail.setText("");
        EditPhone.setText("");
        SpinnerUserType.setSelection(0); // Reset Spinner to default
    }
}
