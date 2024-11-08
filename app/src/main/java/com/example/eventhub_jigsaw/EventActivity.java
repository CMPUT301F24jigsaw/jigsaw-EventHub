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

/**
 * EventActivity is a Fragment that displays a list of events and provides functionality
 * to remove all events from the list.
 */

public class EventActivity extends Fragment {

    private List<Events> eventList; // Data structure to store list of event item objects
    private EventAdapter adapter;

    /**
     * Called to initialize the fragment's user interface view.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view to attach the fragment's UI.
     * @param savedInstanceState Bundle containing the fragment's previously saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.events_page, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample event data
        eventList = new ArrayList<>();
        eventList.add(new Events("Event #1", R.drawable.event_image_placeholder));
        eventList.add(new Events("Event #2", R.drawable.event_image_placeholder));

        // Set up the adapter with the sample data
        EventAdapter adapter = new EventAdapter(eventList);
        recyclerView.setAdapter(adapter);

        Button removeAllButton = view.findViewById(R.id.bottomButton);
        removeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllEvents();
            }
        });

        return view;
    }

    /**
     * Removes all events from the event list and updates the adapter.
     * Shows a message if there are no events to remove.
     */

    private void removeAllEvents() {

        if (eventList.isEmpty()) {
            Toast.makeText(getContext(), "No events to remove.", Toast.LENGTH_SHORT).show();
            return; // Exit the method if there are no events
        }
        // Clear the event list
        eventList.clear();
        // Refresh adapter
        adapter.notifyDataSetChanged();

        // Optionally, show a message or an empty state
        Toast.makeText(getContext(), "All events removed.", Toast.LENGTH_SHORT).show();
    }
}
