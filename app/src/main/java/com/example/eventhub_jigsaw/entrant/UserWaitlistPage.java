package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.provider.Settings;
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
 * UserWaitlistPage displays the list of events the user is on the waitlist for.
 * Fetches event details from DB and displays them in a RecyclerView.
 */

public class UserWaitlistPage extends Fragment {

    private FirebaseFirestore db;
    private String userId;
    private RecyclerView eventsRecyclerView;
    private TextView noEventsText;
    private WaitlistAdapter adapter;
    private List<Event> eventsList = new ArrayList<>();

    /**
     * Called when the fragment's view is created.
     *
     * @param inflater For inflate views.
     * @param container The fragment's UI.
     * @param savedInstanceState
     * @return The inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_waitlist_page, container, false);

        db = FirebaseFirestore.getInstance();
        userId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        noEventsText = view.findViewById(R.id.no_events_text);

        adapter = new WaitlistAdapter(eventsList);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setAdapter(adapter);

        fetchRegisteredEvents();

        return view;
    }

    /**
     * Fetches the user's registered events from Firestore.
     */
    private void fetchRegisteredEvents() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("waitingList")) {
                        List<String> registeredEventIds = (List<String>) documentSnapshot.get("waitingList");
                        if (registeredEventIds != null && !registeredEventIds.isEmpty()) {
                            fetchEventDetails(registeredEventIds);
                        } else {
                            showNoEventsMessage();
                        }
                    } else {
                        showNoEventsMessage();
                    }
                })
                .addOnFailureListener(e -> showNoEventsMessage());
    }

    /**
     * Fetches the event details from DB and updates the RecyclerView.
     *
     * @param eventIds List of event IDs.
     */
    private void fetchEventDetails(List<String> eventIds) {
        for (String eventId : eventIds) {
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("eventName");
                            String date = documentSnapshot.getString("eventDate");
                            String description = documentSnapshot.getString("description");
                            String imageURL = documentSnapshot.getString("imageID");

                            Event event = new Event(name, date, description);
                            event.setEventID(eventId);
                            event.setImageID(imageURL);

                            eventsList.add(event);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    /**
     * Displays a message when there are no waitlisted events.
     */
    private void showNoEventsMessage() {
        noEventsText.setVisibility(View.VISIBLE);
        eventsRecyclerView.setVisibility(View.GONE);
    }
}
