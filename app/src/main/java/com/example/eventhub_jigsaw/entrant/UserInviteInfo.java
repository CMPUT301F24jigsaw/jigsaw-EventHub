package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.admin.LoadingFragment;

public class UserInviteInfo extends Fragment {

    private TextView eventNameTextView;
    private TextView eventDateTextView;
    private TextView eventDescriptionTextView;
    private Button acceptButton;
    private Button declineButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_inviteinfo, container, false);

        // Initialize views
        eventNameTextView = view.findViewById(R.id.eventnameInfo_user);
        eventDateTextView = view.findViewById(R.id.eventdate_user);
        eventDescriptionTextView = view.findViewById(R.id.eventdescription_user);

        acceptButton = view.findViewById(R.id.AcceptButton);
        declineButton = view.findViewById(R.id.DeclineButton);

        // Retrieve data from the bundle
        if (getArguments() != null) {
            String eventName = getArguments().getString("event_name");
            String eventDescription = getArguments().getString("event_description");
            String eventDate = getArguments().getString("event_date");
            String eventID = getArguments().getString("event_id");

            // Set the data to views
            eventNameTextView.setText(eventName);
            eventDateTextView.setText(eventDate);
            eventDescriptionTextView.setText(eventDescription);
        }

        // Handle Accept Button Click
        acceptButton.setOnClickListener(v -> {
            // TODO: Add logic to handle the event acceptance
            // For now, just navigate to the loading screen
            navigateToLoadingScreen();
        });

        // Handle Decline Button Click
        declineButton.setOnClickListener(v -> {
            // Navigate to the loading screen
            navigateToLoadingScreen();

            // Delay for 2 seconds and then go back to the invites page
            new android.os.Handler().postDelayed(() -> {
                // Simulate removal of the declined event and return to the invites list
                getParentFragmentManager().popBackStack(); // Removes LoadingFragment
                getParentFragmentManager().popBackStack(); // Removes UserInviteInfo
            }, 2000); // 2-second delay
        });

        return view;
    }

    private void navigateToLoadingScreen() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoadingFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
