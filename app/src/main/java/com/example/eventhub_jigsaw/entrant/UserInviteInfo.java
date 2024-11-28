package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.admin.LoadingFragment;

public class UserInviteInfo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_inviteinfo, container, false);

        // Retrieve data passed to this fragment
        Bundle args = getArguments();
        String eventName = args != null ? args.getString("event_name") : null;
        int eventImage = args != null ? args.getInt("event_image", 0) : 0;
        String eventAddress = args != null ? args.getString("event_address") : null;
        String eventDate = args != null ? args.getString("event_date") : null;

        // Set up the UI elements with the received data
        ImageView eventImageView = view.findViewById(R.id.eventInfoImage_user);
        TextView eventNameView = view.findViewById(R.id.eventnameInfo_user);
        TextView eventAddressView = view.findViewById(R.id.eventaddress_user);
        TextView eventDateView = view.findViewById(R.id.eventdate_user);

        if (eventImage != 0) {
            eventImageView.setImageResource(eventImage);
        }
        eventNameView.setText(eventName);
        eventAddressView.setText(eventAddress);
        eventDateView.setText(eventDate);

        // Accept Button
        Button acceptButton = view.findViewById(R.id.AcceptButton);
        acceptButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            LoadingFragment loadingFragment = new LoadingFragment();

            Bundle bundle = new Bundle();
            bundle.putString("message", "You have accepted the event: " + eventName);
            loadingFragment.setArguments(bundle);

            transaction.replace(R.id.fragment_container, loadingFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            new Handler().postDelayed(() -> {
                getParentFragmentManager().popBackStack();
                navigateToPage1();
            }, 2000);
        });

        // Decline Button
        Button declineButton = view.findViewById(R.id.DeclineButton);
        declineButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            LoadingFragment loadingFragment = new LoadingFragment();

            Bundle bundle = new Bundle();
            bundle.putString("message", "You have declined the event: " + eventName);
            loadingFragment.setArguments(bundle);

            transaction.replace(R.id.fragment_container, loadingFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            new Handler().postDelayed(() -> {
                getParentFragmentManager().popBackStack();
                navigateToPage1();
            }, 2000);
        });

        return view;
    }

    private void navigateToPage1() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserInvitePageActivity()); // Replace with your Page1 fragment class
        transaction.commit();
    }
}
