package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEventActivity extends Fragment {

    private List<OrganizerEventPage> eventList;
    private OrganizerEventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_eventspage, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents_organizer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        adapter = new OrganizerEventAdapter(eventList, getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        // Fetch initial events
        fetchEventsByOrganizer();

        // Add event button logic
        FloatingActionButton addEventButton = view.findViewById(R.id.addButton);
        addEventButton.setOnClickListener(v -> {
            OrganizerAddEvent addEventDialog = new OrganizerAddEvent();
            addEventDialog.setOnEventAddedListener(this::fetchEventsByOrganizer); // Refresh events after adding
            addEventDialog.show(getChildFragmentManager(), "AddEventDialog");
        });

        return view;
    }

    private void fetchEventsByOrganizer() {
        String organizerID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .whereEqualTo("organizerID", organizerID)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error fetching events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String eventName = document.getString("eventName");
                            int placeholderImage = R.drawable.event_image_placeholder;

                            eventList.add(new OrganizerEventPage(eventName, placeholderImage));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
