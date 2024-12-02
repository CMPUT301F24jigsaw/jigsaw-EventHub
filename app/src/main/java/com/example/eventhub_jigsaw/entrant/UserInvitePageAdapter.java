package com.example.eventhub_jigsaw.entrant;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UserInvitePageAdapter extends RecyclerView.Adapter<UserInvitePageAdapter.EventViewHolder> {

    private List<Event> eventList;
    private FragmentManager fragmentManager;
    private String eventDescription;
    private String eventDate;
    private FirebaseFirestore db;

    public UserInvitePageAdapter(List<Event> eventList, FragmentManager fragmentManager) {
        this.eventList = eventList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_itemsinvite, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        eventDescription = event.getDescription();
        eventDate = event.getEventDate();

        db = FirebaseFirestore.getInstance();

        android.util.Log.e("UserInvitePageAdapter", "Event Details:");
        android.util.Log.e("UserInvitePageAdapter", "Name: " + event.getEventName());
        android.util.Log.e("UserInvitePageAdapter", "Description: " + event.getDescription());
        android.util.Log.e("UserInvitePageAdapter", "Date: " + event.getEventDate());

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

        holder.MoreInfo.setOnClickListener(v -> {
            db.collection("events")
                    .whereEqualTo("eventName", event.getEventName())
                    .whereEqualTo("description", event.getDescription())
                    .whereEqualTo("eventDate", event.getEventDate())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String eventId = document.getId(); // Get the event ID

                                // Create a new fragment instance for event details
                                UserInviteInfo infoFragment = new UserInviteInfo();

                                // Pass event details and eventId to the fragment
                                Bundle bundle = new Bundle();
                                bundle.putString("event_name", event.getEventName());
                                bundle.putString("event_description", event.getDescription());
                                bundle.putString("event_date", event.getEventDate());
                                bundle.putString("event_id", eventId); // Pass the event ID
                                bundle.putString("even_image_url", imageURL);
                                infoFragment.setArguments(bundle);

                                // Transition to the fragment
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, infoFragment)
                                        .addToBackStack(null) // Allow navigation back to RecyclerView
                                        .commit();

                                // Hide RecyclerView and show fragment container
                                View recyclerView = ((ViewGroup) v.getRootView()).findViewById(R.id.recyclerViewEvents_user);
                                View fragmentContainer = ((ViewGroup) v.getRootView()).findViewById(R.id.fragment_container);

                                if (recyclerView != null && fragmentContainer != null) {
                                    recyclerView.setVisibility(View.GONE);
                                    fragmentContainer.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            // Handle case where no matching event is found
                            Toast.makeText(v.getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors in querying Firestore
                        Toast.makeText(v.getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        Button MoreInfo;
        TextView eventName;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage_user);
            MoreInfo = itemView.findViewById(R.id.MoreInfo_user);
            eventName = itemView.findViewById(R.id.eventName_user);
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