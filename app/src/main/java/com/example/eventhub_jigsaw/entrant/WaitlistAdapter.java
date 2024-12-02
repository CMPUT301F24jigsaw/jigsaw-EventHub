package com.example.eventhub_jigsaw.entrant;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.WaitlistViewHolder> {

    private final List<Event> events;
    private FirebaseFirestore db;

    String userID;

    public WaitlistAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_waitlist_item, parent, false);
        return new WaitlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDateTextView.setText(event.getEventDate());
        holder.eventDescriptionTextView.setText(event.getDescription());

        db = FirebaseFirestore.getInstance();

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

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class WaitlistViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;
        Button leaveWaitlist;

        public WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_nam);
            eventDateTextView = itemView.findViewById(R.id.event_dat);
            eventDescriptionTextView = itemView.findViewById(R.id.event_descriptio);
            leaveWaitlist = itemView.findViewById(R.id.button_leave_waitlist);
        }
    }
}
