package com.example.eventhub_jigsaw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddEvent extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back
            if (getActivity() != null) {
                getActivity().onBackPressed(); // Trigger back navigation
            }
        });

        view.findViewById(R.id.saveEventButton).setOnClickListener(v -> {
            // Save event logic
        });

//        view.findViewById(R.id.deleteEventButton).setOnClickListener(v -> {
//            // Delete event logic
//        });
    }
}
