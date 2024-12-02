package com.example.eventhub_jigsaw.entrant;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * WaitlistAdapter for displaying events in the user's waitlist.
 * Allows the user to leave the waitlist for an event.
 */

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.WaitlistViewHolder> {

    private final List<Event> events;
    private FirebaseFirestore db;

    String userID;

    /**
     * Constructor for initializing the adapter with a list of events.
     *
     * @param events List of events to display in the RecyclerView.
     */
    public WaitlistAdapter(List<Event> events) {
        this.events = events;
    }

    /**
     * Called to create new ViewHolder instances for RecyclerView items.
     *
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return
     */
    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_waitlist_item, parent, false);
        return new WaitlistViewHolder(view);
    }

    /**
     * Binds event data to a ViewHolder item.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDateTextView.setText(event.getEventDate());
        holder.eventDescriptionTextView.setText(event.getDescription());

        db = FirebaseFirestore.getInstance();

        String imageURL = event.getImageID();

        if (imageURL != null && !imageURL.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images/events/" + imageURL);

            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                loadImageWithoutGlide(holder.eventImage, downloadUrl);
            }).addOnFailureListener(e -> {
                Log.e("FirebaseStorage", "Error fetching download URL", e);

            });
        }

        holder.leaveWaitlist.setOnClickListener(v -> {

            String eventId = event.getEventID();
            userID = Settings.Secure.getString(v.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);


            db.collection("events").document(eventId)
                    .update("waitingList", FieldValue.arrayRemove(userID))
                    .addOnSuccessListener(aVoid -> {
                        // Remove eventId from user's waitingList
                        db.collection("users").document(userID)
                                .update("waitingList", FieldValue.arrayRemove(eventId))
                                .addOnSuccessListener(aVoid2 -> {
                                    // Optionally, remove the event from the list and refresh the adapter
                                    events.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, events.size());
                                    Log.d("WaitlistAdapter", "Successfully removed from both waiting lists.");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure in removing eventId from user
                                    Log.e("WaitlistAdapter", "Failed to remove event from user's waitlist: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure in removing userID from event
                        Log.e("WaitlistAdapter", "Failed to remove user from event's waitlist: " + e.getMessage());
                    });
        });
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The number of events in the list.
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    static class WaitlistViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;
        Button leaveWaitlist;
        ImageView eventImage;

        /**
         * ViewHolder to bind event views for each item.
         *
         * @param itemView The view for the individual item.
         */
        public WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_nam);
            eventDateTextView = itemView.findViewById(R.id.event_dat);
            eventDescriptionTextView = itemView.findViewById(R.id.event_descriptio);
            leaveWaitlist = itemView.findViewById(R.id.button_leave_waitlist);
            eventImage = itemView.findViewById(R.id.eventImage);
        }
    }

    private void loadImageWithoutGlide(ImageView imageView, String imageUrl) {
        new Thread(() -> {
            try {
                // Open a connection to the URL
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                // Decode the input stream to a Bitmap
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Set the Bitmap to the ImageView on the main thread
                imageView.post(() -> imageView.setImageBitmap(bitmap));

            } catch (Exception e) {
                Log.e("LoadImage", "Error loading image", e);
                // Set fallback image on error
            }
        }).start();
    }
}
