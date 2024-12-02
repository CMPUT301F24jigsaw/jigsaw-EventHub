package com.example.eventhub_jigsaw.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.Event;
import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.entrant.UserInvitePageAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends Fragment {

    private ArrayList<Event> eventList; // Declare eventList as a class variable
    private UserInvitePageAdapter adapter;
    private int lastClickedPosition = -1;

    // Database
    FirebaseFirestore db;
    CollectionReference eventsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.events_page, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        eventList = new ArrayList<>();


        // Set up the adapter with the sample data
        EventAdapter adapter = new EventAdapter(eventList, event -> {
            // This block will be executed when an event item is clicked
            DeleteEventDialog deleteEventFragment = new DeleteEventDialog();

            Bundle args = new Bundle();
            args.putSerializable("event", event);

            // Set the arguments to the fragment
            deleteEventFragment.setArguments(args);

            // Navigate to the DeleteEventDialog fragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flFragment, deleteEventFragment);
            transaction.addToBackStack(null); // Allow back navigation
            transaction.commit();

        });

        recyclerView.setAdapter(adapter);



        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot document : task.getResult()) {
                    String id = document.getId(); // This is the document ID
                    Event event = document.toObject(Event.class);

                    if (event != null) {
                        event.setId(id); // Add the ID to your Facility object if necessary
                        event.setFacilityId(id);  // Add the ID to your Facility object if necessary
                        Log.d("Firestore", "Event ID: " + id + ", Event Name: " + event.getEventName());
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter after adding events
            } else {
                Log.e("Firestore", "Failed to fetch documents", task.getException());
            }
        });



        // create snapshot listener to update database live
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        try {
                            String eventName = doc.getString("eventName");
                            String eventDate = doc.getString("eventDate");
                            String organizerId = doc.getString("organizerID");
                            String description = doc.getString("description");
                            int maxAttendees = doc.get("maxAttendees", int.class);
                            Log.d("Firestore", String.format("Event(%s, %s, %s) fetched", eventName, eventDate, organizerId));
                            eventList.add(new Event(eventName, eventDate, organizerId, maxAttendees, description));
                        } catch (Exception e) {
                            Log.e("Firestore", "Error processing document: " + doc.getId(), e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "No facilities found");
                }
            }
        });

        Button removeEventButton = view.findViewById(R.id.bottomButton);
        removeEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickedPosition != -1) {
                    Event event = eventList.remove(lastClickedPosition);
                    removeEvent(event);
                }
            }
        });

        return view;
    }


    private void removeEvent(Event event) {
        if (eventList.isEmpty()) {
            Toast.makeText(getContext(), "No events to remove.", Toast.LENGTH_SHORT).show();
            return; // Exit the method if there are no events
        }

        lastClickedPosition = -1; // reset last clicked position

        eventList.remove(event); // Clear the event list
        adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed

        // remove permanently from database
        eventsRef.document(event.getId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firestore", "Document deleted successfully");
            } else {
                Log.e("Firestore", "Failed to delete document", task.getException());
            }
        });


        // Optionally, show a message or an empty state
        Toast.makeText(getContext(), "Event removed successfully!", Toast.LENGTH_SHORT).show();
    }
}
