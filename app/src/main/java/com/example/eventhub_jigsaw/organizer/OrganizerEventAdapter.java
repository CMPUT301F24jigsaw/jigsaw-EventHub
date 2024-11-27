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

import java.util.List;
import androidx.fragment.app.FragmentManager;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.entrant.UserInviteInfo;
import com.example.eventhub_jigsaw.entrant.UserInvitePage;

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.EventViewHolder> {

    private List<UserInvitePage> eventList;
    private FragmentManager fragmentManager;

    public OrganizerEventAdapter(List<UserInvitePage> eventList, FragmentManager fragmentManager) {
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
        UserInvitePage event = eventList.get(position);
        holder.eventName.setText(event.getEventName_user());
        holder.eventImage.setImageResource(event.getEventImage_user());

        holder.MoreInfo.setOnClickListener(v -> {
            // Create a new fragment instance
            UserInviteInfo infoFragment = new UserInviteInfo();

            // Pass data to the fragment
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getEventName_user());
            bundle.putInt("event_image", event.getEventImage_user());
            bundle.putString("event_address", "123 Event Street"); // Example address
            bundle.putString("event_date", "2024-11-30"); // Example date
            infoFragment.setArguments(bundle);



            // Hide RecyclerView and show the fragment container
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, infoFragment)
                    .addToBackStack(null) // Add to back stack for navigation
                    .commit();

            // Programmatically hide RecyclerView and show fragment container
            View recyclerView = ((ViewGroup) v.getRootView()).findViewById(R.id.recyclerViewEvents_user);
            View fragmentContainer = ((ViewGroup) v.getRootView()).findViewById(R.id.fragment_container);

            recyclerView.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
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
}