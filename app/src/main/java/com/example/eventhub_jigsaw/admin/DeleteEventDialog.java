package com.example.eventhub_jigsaw.admin;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteEventDialog extends Fragment {

    private static final String ARG_EVENT = "event";
    private Event event;

    public static DeleteEventDialog newInstance(Event event){
        DeleteEventDialog fragment = new DeleteEventDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = (Event) getArguments().getSerializable(ARG_EVENT); //retrieve the event object
        } else {
            throw new IllegalStateException("Event data not provided!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_event_admin, container, false);

        if (event == null) {
            Toast.makeText(getContext(), "Event data is missing!", Toast.LENGTH_SHORT).show();
            return view;
        }

        ImageView eventImage = view.findViewById(R.id.event_image_remove);
        Button deleteImagebutton = view.findViewById(R.id.delete_event_image_button);
        ImageView QRcodeImage = view.findViewById(R.id.event_qrcode_remove);
        Button deleteQRCodeButton = view.findViewById(R.id.delete_event_qrcode_button);
        Button saveButton = view.findViewById(R.id.save_event_button);
        TextView eventName = view.findViewById(R.id.event_name_text);
        Button deleteEvent = view.findViewById(R.id.delete_event_admin);

        // Load Event Image from URL
        loadEventImage(event.getEventImageUrl(), eventImage);

        // Retrieve event data from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            Event event = (Event) bundle.getSerializable("event"); // Retrieve the serialized Event object
            if (event != null) {
                String eventname = event.getEventName();// Access eventName using the getter method
                eventName.setText(eventname); // Set the TextView to the event name

                Log.d("DeleteEventDialog", "Event Name: " + eventName);
            } else {
                Log.e("DeleteEventDialog", "Event is null!");
            }
        } else {
            Log.e("DeleteEventDialog", "Bundle is null!");
        }


        // Fetch QRCode data from Firestore

        // MAY HAVE TO FETCH IMAGEID FIRST IN PLACE OF ORGANIZERID
        String eventID = event.getId();
        if (eventID != null){
            fetchQRCodeFromDatabase(eventID, QRcodeImage);
        } else {
            Toast.makeText(getContext(), "Event ID is missing.", Toast.LENGTH_SHORT).show();
        }

        deleteImagebutton.setOnClickListener(v -> {
            eventImage.setImageResource(R.drawable.ic_events_background);
            Toast.makeText(getContext(), "Event image deleted!", Toast.LENGTH_SHORT).show();
        });

        deleteQRCodeButton.setOnClickListener(v -> {
            QRcodeImage.setImageResource(R.drawable.ic_events_background);
            Toast.makeText(getContext(), "QR code deleted!", Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
            // Navigate back to the previous fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        deleteEvent.setOnClickListener(v -> {
            if (event != null && event.getId() != null) {
                String eventId = event.getId();

                // Delete the event from Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("events").document(eventId)
                        .delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Event deleted successfully.", Toast.LENGTH_SHORT).show();

                            // Navigate back to the previous fragment or update UI as needed
                            requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Event details are missing. Cannot delete.", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void fetchQRCodeFromDatabase(String eventID, ImageView qrCodeImageView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("qrCode")) {
                        String qrCodeBase64 = documentSnapshot.getString("qrCode");
                        if (qrCodeBase64 != null) {
                            Bitmap qrCodeBitmap = convertBase64ToBitmap(qrCodeBase64);
                            qrCodeImageView.setImageBitmap(qrCodeBitmap);
                        }
                    } else {
                        Toast.makeText(getContext(), "QR code not found for this event!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void loadEventImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(imageUrl) // URL of the image
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache for faster loading
                    .placeholder(R.drawable.ic_events_background) // Placeholder while loading
                    .error(R.drawable.event_image_placeholder) // Fallback in case of an error
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_events_background); // Fallback if URL is null or empty
        }
    }

    private Bitmap convertBase64ToBitmap(String base64String){
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }



}
