package com.example.eventhub_jigsaw;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.eventhub_jigsaw.entrant.UserHomePage;
import com.example.eventhub_jigsaw.organizer.OrganizerHomePage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FusedLocationProviderClient fusedLocationClient;

    double latitude;
    double longitude;

    public interface OnLocationReceivedListener {
        void onLocationReceived(double latitude, double longitude);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore and retrieve userID
        db = FirebaseFirestore.getInstance();
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize image selection and upload components
        selectImage = new SelectImage(activityResultLauncher, selectedImageView);
        uploadImage = new UploadImage();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        long phone;
        try {
            phone = Long.parseLong(phoneStr); // Parse phone number as long
        } catch (NumberFormatException e) {
            EditPhone.setError("Invalid phone number!");
            return;
        }

        getUserLocation(new OnLocationReceivedListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                // Handle image upload logic and user registration here
                String imageUrl = null;
                // Check if an image was selected
                if (selectedImageUri != null) {
                    // If an image was selected, upload it
                    uploadImage.uploadImage(selectedImageUri, new UploadImage.OnUploadCompleteListener() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            // Image uploaded successfully, save the URL to Firestore or use it in the user object
                            saveUserToFirestore(userID, name, email, phone, role, imageUrl, latitude, longitude);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // Handle failure in image upload
                            Toast.makeText(UserSignUp.this, "Failed to upload image: " + errorMessage, Toast.LENGTH_SHORT).show();
                            // Proceed with null image URL if upload fails
                            saveUserToFirestore(userID, name, email, phone, role, null, latitude, longitude);
                        }
                    }, userID);
                } else {
                    // No image selected, proceed with saving the user to Firestore without an image
                    saveUserToFirestore(userID, name, email, phone, role, null, latitude, longitude);
                }
            }
        });
    }


    private void saveUserToFirestore(String userID, String name, String email, long phone, String role, String imageUrl, double latitude, double longitude) {
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
        user.setLatitude(latitude);
        user.setLongitude(longitude);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("UserSignUp", "Location permission granted");
            } else {
                Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Checks if the location permission is granted and fetches the location
    private void getUserLocation(OnLocationReceivedListener listener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Provide default values for latitude and longitude
            listener.onLocationReceived(0.0, 0.0);  // Default values when location is not granted
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        listener.onLocationReceived(latitude, longitude); // pass the values as arguments
                    } else {
                        Toast.makeText(this, "Failed to retrieve location. Please ensure location is enabled.", Toast.LENGTH_SHORT).show();
                        listener.onLocationReceived(0.0, 0.0);  // Default values if location is null
                    }
                });
    }



}
