package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.entrant.UserInvitePage;
import com.example.eventhub_jigsaw.entrant.UserInvitePageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEventActivity extends Fragment {

    private List<UserInvitePage> eventList; // Declare eventList as a class variable
    private OrganizerEventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_eventspage, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents_organizer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample event data
        eventList = new ArrayList<>();
        eventList.add(new UserInvitePage("Event #1", R.drawable.event_image_placeholder));
        eventList.add(new UserInvitePage("Event #2", R.drawable.event_image_placeholder));

        // Set the adapter for the RecyclerView
        UserInvitePageAdapter adapter = new UserInvitePageAdapter(eventList, getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        // Find the FloatingActionButton and set an OnClickListener
        FloatingActionButton addEventButton = view.findViewById(R.id.addButton);
        addEventButton.setOnClickListener(v -> {
            // Create and show the AddEvent DialogFragment
            OrganizerAddEvent addEventDialog = new OrganizerAddEvent();
            // Show the DialogFragment using the FragmentManager
            addEventDialog.show(getChildFragmentManager(), "AddEventDialog");
        });

        return view;
    }
}
