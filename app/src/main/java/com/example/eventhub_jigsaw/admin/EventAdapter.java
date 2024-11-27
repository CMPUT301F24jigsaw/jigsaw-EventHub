package com.example.eventhub_jigsaw.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;

import java.util.List;

/**
 * EventAdapter is a RecyclerView adapter that binds event data to views displayed in the RecyclerView.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Events> eventList;
    /**
     * Constructor for the EventAdapter.
     *
     * @param eventList The list of events to display in the RecyclerView.
     */
    public EventAdapter(List<Events> eventList) {
        this.eventList = eventList;
    }
    /**
     * Called when RecyclerView needs a new {@link EventViewHolder} to represent an item.
     *
     * @param parent   The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new EventViewHolder that holds a View for an event item.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_event, parent, false);
        return new EventViewHolder(view);
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder to update with the event data.
     * @param position The position of the event item in the list.
     */
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
    /**
     * ViewHolder class for holding views for each event item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage, viewIcon;
        TextView eventName;

        /**
         * Constructor for the EventViewHolder.
         *
         * @param itemView The view representing an event item.
         */

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            viewIcon = itemView.findViewById(R.id.ViewIcon);
            eventName = itemView.findViewById(R.id.eventName);
        }
    }
}
