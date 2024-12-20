package com.example.eventhub_jigsaw.entrant;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationActivity handles the display and clearing of user notifications.
 * Interacts with DB to fetch and update notification preferences.
 */

public class NotificationActivity extends Fragment {

    private List<String> notificationList;
    private NotificationAdapter adapter;
    private FirebaseFirestore db;
    private String userID;
    private boolean notificationsEnabled;

    /**
     * Initializes the fragment view and sets up notification fetching and clear functionality.
     *
     * @param inflater           For inflating views.
     * @param container          Parent view group.
     * @param savedInstanceState Saved instance state.
     * @return The fragment's root view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_notifications, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();

        // Initialize ListView and adapter
        ListView listView = view.findViewById(R.id.Notifications);
        adapter = new NotificationAdapter(requireContext(), notificationList);
        listView.setAdapter(adapter);

        // Get reference to the clear button
        Button clearButton = view.findViewById(R.id.clearButton);

        // Fetch notifications and current preference
        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        fetchNotifications();

        // Handle button click
        clearButton.setOnClickListener(v -> {
            db.collection("users").document(userID)
                    .update("organizerNotification", false)
                    .addOnSuccessListener(aVoid -> {
                        // Optional: Provide feedback or logging on success
                        Log.d("Firestore", "Notifications disabled successfully.");
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Log.e("Firestore", "Error disabling notifications", e);
                    });
        });
        return view;
    }

    /**
     * Fetches notifications for the current user from DB.
     */

    private void fetchNotifications() {
        db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Check if 'organizerNotification' is true
                        Boolean organizerNotification = documentSnapshot.getBoolean("organizerNotification");
                        if (organizerNotification != null && organizerNotification) {
                            // Fetch notifications if 'organizerNotification' is true
                            List<String> notifications = (List<String>) documentSnapshot.get("notifications");
                            if (notifications != null) {
                                notificationList.clear();
                                notificationList.addAll(notifications);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            // Optional: Handle the case when 'organizerNotification' is false
                            Toast.makeText(requireContext(), "Notifications are disabled", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
                });
    }


}
