package com.example.eventhub_jigsaw.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventhub_jigsaw.R;

public class UserMyProfileEdit extends DialogFragment {

    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newUsername, String newEmail);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the dialog layout
        View view = inflater.inflate(R.layout.user_myprofile_edit, container, false);

        // Find input fields and button
        TextView inputUsername = view.findViewById(R.id.edit_username_field);
        TextView inputEmail = view.findViewById(R.id.edit_email_field);
        Button saveButton = view.findViewById(R.id.save_button);

        // Handle save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = inputUsername.getText().toString();
                String newEmail = inputEmail.getText().toString();

                // Validate inputs
                if (newUsername.isEmpty() || newEmail.isEmpty()) {
                    // Optional: Show an error message if needed
                    return;
                }

                // Send data back to the parent fragment
                OnProfileUpdateListener listener = (OnProfileUpdateListener) getTargetFragment();
                if (listener != null) {
                    listener.onProfileUpdate(newUsername, newEmail);
                }

                // Close the dialog
                dismiss();
            }
        });

        return view;
    }
}

