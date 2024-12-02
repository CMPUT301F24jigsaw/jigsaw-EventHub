package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * EventActivity is a Fragment that manages the display and working of events.
 * It fetches event data from DB, and provides an interface for removing events.
 */

public class EventActivity extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView eventsRecyclerView;
    private TextView noEventsText;
    private EventAdapter adapter;
    private final List<Event> eventsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_page, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and TextView
        eventsRecyclerView = view.findViewById(R.id.recyclerViewEvents);
        noEventsText = view.findViewById(R.id.no_events_text);

        // Pass FragmentManager to EventAdapter
        adapter = new EventAdapter(eventsList, getChildFragmentManager());
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setAdapter(adapter);

        // Fetch events from Firestore
        fetchAllEvents();

        return view;
    }

    private void fetchAllEvents() {
        db.collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("EventActivity", "Fetched " + queryDocumentSnapshots.size() + " events.");
                    if (!queryDocumentSnapshots.isEmpty()) {
                        eventsList.clear();
                        for (var document : queryDocumentSnapshots) {
                            try {
                                String id = document.getId();
                                String name = document.getString("eventName");
                                String date = document.getString("eventDate");
                                String description = document.getString("description");
                                String imageURL = document.getString("imageID");
                                Long maxAttendeesLong = document.getLong("maxAttendees");
                                int maxAttendees = maxAttendeesLong != null ? maxAttendeesLong.intValue() : 0;

                                Event event = new Event(id, name, date, null, maxAttendees, description);
                                event.setImageID(imageURL);
                                eventsList.add(event);
                                Log.d("EventActivity", "Added event: " + name);
                            } catch (Exception e) {
                                Log.e("EventActivity", "Error processing event document", e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        showEvents();
                    } else {
                        Log.d("EventActivity", "No events found.");
                        showNoEventsMessage();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventActivity", "Error fetching events from Firestore", e);
                    showNoEventsMessage();
                });
    }

    private void showNoEventsMessage() {
        noEventsText.setVisibility(View.VISIBLE);
        eventsRecyclerView.setVisibility(View.GONE);
    }

    private void showEvents() {
        noEventsText.setVisibility(View.GONE);
        eventsRecyclerView.setVisibility(View.VISIBLE);
    }
}
