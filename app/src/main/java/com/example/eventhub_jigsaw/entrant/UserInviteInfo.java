package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

        Button declineButton = view.findViewById(R.id.DeclineButton);
        declineButton.setOnClickListener(v -> {
            // Navigate to the loading screen
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new LoadingFragment());
            transaction.addToBackStack(null);
            transaction.commit();

            // Delay for 2 seconds and then go back to the Invites page
            new Handler().postDelayed(() -> {
                // Simulate removal of the declined event and return to the Invites list
                getParentFragmentManager().popBackStack(); // Removes LoadingFragment
                getParentFragmentManager().popBackStack(); // Removes UserInviteInfo
            }, 2000); // 2-second delay

        });


        return view;
    }
}
