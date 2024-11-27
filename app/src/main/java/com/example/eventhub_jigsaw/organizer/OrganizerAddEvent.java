package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;

public class OrganizerAddEvent extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.organizer_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Close the dialog without going back to the previous page
            dismiss(); // This dismisses the DialogFragment
        });

        // Handle saving event logic (example)
        view.findViewById(R.id.saveEventButton).setOnClickListener(v -> {
            // Save event logic (e.g., save to database, etc.)
            // You can retrieve arguments passed to this fragment:
            String eventName = getArguments() != null ? getArguments().getString("event_name") : "No Name";
            int eventImage = getArguments() != null ? getArguments().getInt("event_image") : R.drawable.event_image_placeholder;
            // Proceed with saving logic
        });
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        // Handle any cleanup if needed when the dialog is dismissed
    }
}
