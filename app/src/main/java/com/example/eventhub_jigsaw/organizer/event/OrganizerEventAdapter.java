package com.example.eventhub_jigsaw.organizer;

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

import java.util.List;

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.EventViewHolder> {

    private List<OrganizerEventPage> eventList;
    private FragmentManager fragmentManager;

    // Constructor to pass event list and FragmentManager
    public OrganizerEventAdapter(List<OrganizerEventPage> eventList, FragmentManager fragmentManager) {
        this.eventList = eventList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each event item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_itemsinvite, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        OrganizerEventPage event = eventList.get(position);
        holder.eventName.setText(event.getEventName_organizer());
        holder.eventImage.setImageResource(event.getEventImage_organizer());

        holder.MoreInfo.setOnClickListener(v -> {
            // Create a new DialogFragment instance
            OrganizerEventInfo infoFragment = new OrganizerEventInfo();

            // Pass data to the DialogFragment
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getEventName_organizer());
            bundle.putInt("event_image", event.getEventImage_organizer());
            infoFragment.setArguments(bundle);

            // Show the DialogFragment
            infoFragment.show(fragmentManager, "event_info_dialog");
        });

//        holder.SampleUser.setOnClickListener(v -> {
//            OrganizerSampleEntrant sampleUsersDialog = new OrganizerSampleEntrant();
//            Bundle bundle = new Bundle();
//            bundle.putString("event_id", event.getEventId()); // Pass event ID
//            sampleUsersDialog.setArguments(bundle);
//            sampleUsersDialog.show(fragmentManager, "sample_users_dialog");
//
//        });
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
        Button SampleUser;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            eventImage = itemView.findViewById(R.id.eventImage_organizer);
            MoreInfo = itemView.findViewById(R.id.MoreInfo_organizer);
            eventName = itemView.findViewById(R.id.eventName_organizer);
            SampleUser = itemView.findViewById(R.id.button_sample_User);
        }
    }
}
