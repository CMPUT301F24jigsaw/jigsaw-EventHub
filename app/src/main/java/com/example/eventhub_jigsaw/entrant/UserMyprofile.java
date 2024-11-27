package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventhub_jigsaw.R;

public class UserMyprofile extends Fragment implements UserMyProfileEdit.OnProfileUpdateListener {
    private TextView Text_username;
    private TextView Text_email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_myprofile, container, false);

        // Set initial user information
        String username = "Ashwin Shanmugam";
        String email = "ashwinshanmugam5103@gmail.com";

        // Find and set the TextViews
        Text_username = view.findViewById(R.id.username_field);
        Text_email = view.findViewById(R.id.email_field);

        Text_username.setText(username);
        Text_email.setText(email);

        // Find and handle the Edit button
        Button Button_edit = view.findViewById(R.id.edit_button);
        Button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the edit dialog
                UserMyProfileEdit editDialog = new UserMyProfileEdit();
                editDialog.setTargetFragment(UserMyprofile.this, 0); // Set target for communication
                editDialog.show(getParentFragmentManager(), "edit_profile_dialog");
            }
        });

        return view;
    }

    // Callback method to handle updates from the dialog
    @Override
    public void onProfileUpdate(String newUsername, String newEmail) {
        Text_username.setText(newUsername);
        Text_email.setText(newEmail);
    }
}

