package com.example.eventhub_jigsaw.entrant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private final List<Event> events;

    public EventsAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDateTextView.setText(event.getEventDate());
        holder.eventDescriptionTextView.setText(event.getDescription());


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
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventDateTextView = itemView.findViewById(R.id.event_date);
            eventDescriptionTextView = itemView.findViewById(R.id.event_description);
            eventImage= itemView.findViewById(R.id.eventImage);
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