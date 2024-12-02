package com.example.eventhub_jigsaw.organizer.event;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.organizer.OrganizerCanceledUser;
import com.example.eventhub_jigsaw.organizer.OrganizerSelectedUser;

import java.util.List;

/**
 * OrganizerEventAdapter used for displaying events in a list format.
 * Allows interaction with event details through buttons in the UI.
 */

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.EventViewHolder> {

    private List<OrganizerEventPage> eventList;
    private FragmentManager fragmentManager;

    /**
     * Constructor for initializing the adapter with event data and fragment manager.
     *
     * @param eventList       List of events to be displayed.
     * @param fragmentManager To handle dialog fragments.
     */
    public OrganizerEventAdapter(List<OrganizerEventPage> eventList, FragmentManager fragmentManager) {
        this.eventList = eventList;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Inflates the layout for each event item in the RecyclerView.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each event item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_itemsinvite, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds event data to each view holder.
     *
     * @param holder
     * @param position Position of the event in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        OrganizerEventPage event = eventList.get(position);

        // Set the event name
        holder.eventName.setText(event.getEventName_organizer());

        // Set the event image using Bitmap
        Bitmap eventImageBitmap = event.getEventImageBitmap_organizer();
        if (eventImageBitmap != null) {
            holder.eventImage.setImageBitmap(eventImageBitmap);
        } else {
            // Set a placeholder image if Bitmap is null
            holder.eventImage.setImageResource(R.drawable.event_image_placeholder); // Replace with your placeholder resource ID
        }

        holder.MoreInfo.setOnClickListener(v -> {
            // Create a new DialogFragment instance
            OrganizerEventInfo infoFragment = new OrganizerEventInfo();

            // Pass data to the DialogFragment
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getEventName_organizer());
            infoFragment.setArguments(bundle);

            // Show the DialogFragment
            infoFragment.show(fragmentManager, "event_info_dialog");
        });

        holder.selectedUser.setOnClickListener(v -> {
            OrganizerSelectedUser selectedUser = new OrganizerSelectedUser();
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getEventName_organizer());
            selectedUser.setArguments(bundle);
            selectedUser.show(fragmentManager, "event_selected_user");
        });

        holder.canceledUsers.setOnClickListener(v -> {
            OrganizerCanceledUser canceledUser = new OrganizerCanceledUser();
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getEventName_organizer());
            canceledUser.setArguments(bundle);
            canceledUser.show(fragmentManager, "event_canceled_user");
        });


    }
    
    @Override
    public int getItemCount() {
        // Return the total number of events
        return eventList.size();
    }

    // ViewHolder class to represent individual event items
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        Button MoreInfo;
        TextView eventName;
        Button selectedUser;
        Button canceledUsers;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            eventImage = itemView.findViewById(R.id.eventImage_organizer);
            MoreInfo = itemView.findViewById(R.id.MoreInfo_organizer);
            eventName = itemView.findViewById(R.id.eventName_organizer);
            selectedUser = itemView.findViewById(R.id.button_selected_user);
            canceledUsers = itemView.findViewById(R.id.button_cancelled_user);
        }
    }
}