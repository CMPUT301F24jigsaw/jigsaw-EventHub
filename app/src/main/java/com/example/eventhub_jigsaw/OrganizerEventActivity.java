package com.example.eventhub_jigsaw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEventActivity extends Fragment {

    private List<Events> eventList;
    private OrganizerEventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_page, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        eventList.add(new Events("Event #1", R.drawable.event_image_placeholder));
        eventList.add(new Events("Event #2", R.drawable.event_image_placeholder));
        adapter = new OrganizerEventAdapter(eventList);
        recyclerView.setAdapter(adapter);

        Button removeAllButton = view.findViewById(R.id.bottomButton);
        removeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllEvents();
            }
        });

        Button addEventButton = view.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEvent("New Event", R.drawable.event_image_placeholder);
            }
        });

        return view;
    }

    private void removeAllEvents() {
        if (eventList.isEmpty()) {
            Toast.makeText(getContext(), "No events to remove.", Toast.LENGTH_SHORT).show();
            return;
        }
        eventList.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "All events removed.", Toast.LENGTH_SHORT).show();
    }

    private void addNewEvent(String eventName, int eventImage) {
        Events newEvent = new Events(eventName, eventImage);
        eventList.add(newEvent);
        adapter.notifyItemInserted(eventList.size() - 1);
        Toast.makeText(getContext(), "New event added: " + eventName, Toast.LENGTH_SHORT).show();
    }
}
