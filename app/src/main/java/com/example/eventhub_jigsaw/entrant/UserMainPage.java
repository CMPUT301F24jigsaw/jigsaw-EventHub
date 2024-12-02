package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.R;

public class UserMainPage extends Fragment {

    Button registeredEvents, waitlistEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_main_page, container, false);

        registeredEvents = view.findViewById(R.id.button_registered_events);
        waitlistEvents = view.findViewById(R.id.button_waitlist_events);

        loadFragment(new UserEventPage());

        registeredEvents.setOnClickListener(v -> {
            loadFragment(new UserEventPage());
        });

        waitlistEvents.setOnClickListener(v -> {
            loadFragment(new UserWaitlistPage());
        });

        return view;

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
