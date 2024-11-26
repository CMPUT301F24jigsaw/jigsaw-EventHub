package com.example.eventhub_jigsaw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.OrganizerEventViewHolder> {

    private List<Events> eventList;

    public OrganizerEventAdapter(List<Events> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public OrganizerEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_event, parent, false);
        return new OrganizerEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerEventViewHolder holder, int position) {
        Events event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventImage.setImageResource(event.getEventImage());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void clearEvents() {
        eventList.clear();
        notifyDataSetChanged();
    }

    public static class OrganizerEventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage, viewIcon;
        TextView eventName;

        public OrganizerEventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            viewIcon = itemView.findViewById(R.id.ViewIcon);
            eventName = itemView.findViewById(R.id.eventName);
        }
    }
}
