package com.example.eventhub_jigsaw.entrant;

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

import java.util.ArrayList;
import java.util.List;

public class UserInvitePageActivity extends Fragment {

    private List<UserInvitePage> eventList; // Declare eventList as a class variable
    private UserInvitePageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_invitepage, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        eventList = new ArrayList<>();
        eventList.add(new UserInvitePage("Event #1", R.drawable.event_image_placeholder));
        eventList.add(new UserInvitePage("Event #2", R.drawable.event_image_placeholder));

        adapter = new UserInvitePageAdapter(eventList, getChildFragmentManager());
        recyclerView.setAdapter(adapter);


        return view;
    }

    public void removeEvent(String eventName) {
        // Remove the declined event from the list
        eventList.removeIf(event -> event.getEventName_user().equals(eventName));
        adapter.notifyDataSetChanged(); // Refresh RecyclerView
    }
}

