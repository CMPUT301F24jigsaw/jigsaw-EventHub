package com.example.eventhub_jigsaw.organizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class OrganizerEventInfo extends DialogFragment {

    private ImageView eventQrImage;
    private TextView eventNameTextView;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for the dialog fragment
        View view = inflater.inflate(R.layout.organizers_inviteinfo, container, false);

        // Initialize the views
        eventQrImage = view.findViewById(R.id.eventInfoImage_user);
        eventNameTextView = view.findViewById(R.id.eventnameInfo_user);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        view.findViewById(R.id.button_ViewWaitlist).setOnClickListener(v -> {
            // Open the WaitlistDialogFragment
            OrganizerViewWaitlist waitlistDialog = new OrganizerViewWaitlist();
            waitlistDialog.show(getParentFragmentManager(), "WaitlistDialog");
        });

        // Retrieve event name and organizer ID passed from the previous fragment
        Bundle args = getArguments();
        if (args != null) {
            String eventName = args.getString("event_name");
            String organizerId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            // Fetch event data from Firestore using name and organizer ID
            fetchEventDetails(eventName, organizerId);
        }

        // Return the view for the dialog
        return view;
    }

    private void fetchEventDetails(String eventName, String organizerId) {
        if (eventName == null || organizerId == null) {
            Toast.makeText(getContext(), "Event name or organizer ID is missing", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        // Query Firestore to find the event with the given name and organizer ID
        db.collection("events")
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("organizerID", organizerId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assuming only one result is returned
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Retrieve event details
                            String name = document.getString("eventName");
                            String qrCodeBase64 = document.getString("qrCode");

                            // Display event name
                            eventNameTextView.setText(name);

                            // Decode and display the QR code
                            if (qrCodeBase64 != null && !qrCodeBase64.isEmpty()) {
                                Bitmap qrCodeBitmap = decodeBase64ToBitmap(qrCodeBase64);
                                eventQrImage.setImageBitmap(qrCodeBitmap);
                            } else {
                                Toast.makeText(getContext(), "No QR code found for this event", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                });
    }

    private Bitmap decodeBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // Optionally, customize dialog size or behavior
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
