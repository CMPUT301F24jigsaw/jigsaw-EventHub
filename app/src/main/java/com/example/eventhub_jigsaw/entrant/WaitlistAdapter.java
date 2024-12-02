package com.example.eventhub_jigsaw.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;

import java.util.List;

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.WaitlistViewHolder> {

    private final List<Event> events;

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
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class WaitlistViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;

        public WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_nam);
            eventDateTextView = itemView.findViewById(R.id.event_dat);
            eventDescriptionTextView = itemView.findViewById(R.id.event_descriptio);
        }
    }
}
