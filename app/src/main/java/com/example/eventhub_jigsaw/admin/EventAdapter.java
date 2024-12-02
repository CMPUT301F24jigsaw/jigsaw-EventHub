package com.example.eventhub_jigsaw.admin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> events;
    private final FragmentManager fragmentManager;

    public EventAdapter(List<Event> events, FragmentManager fragmentManager) {
        this.events = events;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDateTextView.setText(event.getEventDate());
        holder.eventDescriptionTextView.setText(event.getDescription());

        if (event == null) {
            Log.e("EventAdapter", "Event object is null");
        } else {
            Log.e("EventAdapter", event.getEventName());
        }

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

        holder.removeButton.setOnClickListener(v -> {
            if (fragmentManager != null) {
                AdminRemoveEventsDetail adminRemoveEventsDetail = new AdminRemoveEventsDetail();
                Bundle bundle = new Bundle();
                bundle.putString("event_id", event.getEventID());
                adminRemoveEventsDetail.setArguments(bundle);
                adminRemoveEventsDetail.show(fragmentManager, "admin_remove_events_details");
            } else {
                Log.e("EventAdapter", "FragmentManager is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;
        ImageView eventImage;
        Button removeButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventDateTextView = itemView.findViewById(R.id.event_date);
            eventDescriptionTextView = itemView.findViewById(R.id.event_description);
            eventImage = itemView.findViewById(R.id.eventImage);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

    private void loadImageWithoutGlide(ImageView imageView, String imageUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                imageView.post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                Log.e("LoadImage", "Error loading image", e);
            }
        }).start();
    }
}
