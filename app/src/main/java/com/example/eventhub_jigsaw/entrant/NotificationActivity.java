package com.example.eventhub_jigsaw.entrant;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends Fragment {

    private List<String> notificationList; // List of notifications
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_notifications, container, false);


        // Sample notifications
        notificationList = new ArrayList<>();
        notificationList.add("You have registered for a new event!");
        notificationList.add("You have scanned a new event!");

        // Set up ListView
        ListView listView = view.findViewById(R.id.Notifications);
        adapter = new NotificationAdapter(requireContext(), notificationList);
        listView.setAdapter(adapter);

        return view;

    }
}

