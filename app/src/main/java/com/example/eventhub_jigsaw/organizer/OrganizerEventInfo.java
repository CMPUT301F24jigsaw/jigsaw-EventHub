package com.example.eventhub_jigsaw.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;

public class OrganizerEventInfo extends DialogFragment {

    private ImageView eventImage;
    private TextView eventName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for the dialog fragment
        View view = inflater.inflate(R.layout.organizers_inviteinfo, container, false);

        // Initialize the views
        eventImage = view.findViewById(R.id.eventInfoImage_user);
        eventName = view.findViewById(R.id.eventnameInfo_user);

        // Retrieve event data passed from the previous fragment
        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("event_name");
            int imageResId = args.getInt("event_image");

            eventName.setText(name);
            eventImage.setImageResource(imageResId);
        }

        // Return the view for the dialog
        return view;
    }

    // Optionally, you can customize dialog size or behavior
    @Override
    public void onStart() {
        super.onStart();
        // Set dialog size (optional)
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
