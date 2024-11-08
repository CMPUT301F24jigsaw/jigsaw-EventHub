package com.example.eventhub_jigsaw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Events> eventList;

    public EventAdapter(List<Events> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Events event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventImage.setImageResource(event.getEventImage());
    }

    /**
     * Update getItemCount method
     * @return length of eventList object
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void clearEvents() {
        eventList.clear(); // Remove all event items
        notifyDataSetChanged(); // refresh adapter view
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage, viewIcon;
        TextView eventName;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            viewIcon = itemView.findViewById(R.id.ViewIcon);
            eventName = itemView.findViewById(R.id.eventName);
        }
    }
}
