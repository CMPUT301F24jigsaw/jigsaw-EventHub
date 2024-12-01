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

public class NotificationActivity extends Fragment {

    private List<String> notificationList; // List of notifications
    private NotificationAdapter adapter;
    private FirebaseFirestore db; // Firestore instance

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_notifications, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize notification list and adapter
        notificationList = new ArrayList<>();
        ListView listView = view.findViewById(R.id.Notifications);
        adapter = new NotificationAdapter(requireContext(), notificationList);
        listView.setAdapter(adapter);

        // Fetch notifications from Firestore
        fetchNotifications();

        return view;
    }

    /**
     * Fetch notifications from Firestore for the current user.
     */
    private void fetchNotifications() {
        // Hardcoded userID for testing; replace this dynamically in production
        String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);



        db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("NotificationActivity", "Document data: " + documentSnapshot.getData());

                        List<String> notifications = (List<String>) documentSnapshot.get("notifications");
                        if (notifications != null) {
                            notificationList.clear();
                            notificationList.addAll(notifications);
                            adapter.notifyDataSetChanged();
                            Log.d("NotificationActivity", "Notifications loaded successfully.");
                        } else {
                            Toast.makeText(requireContext(), "No notifications found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "User document not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationActivity", "Error fetching notifications", e);
                    Toast.makeText(requireContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                });
    }

}

