package com.example.eventhub_jigsaw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProfileArrayAdapter extends ArrayAdapter<com.example.eventhub_jigsaw.Profiles> {

    public ProfileArrayAdapter(Context context, ArrayList<com.example.eventhub_jigsaw.Profiles> cities) {
        super(context, 0, cities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null) ?
                LayoutInflater.from(getContext()).inflate(R.layout.profiles_content, parent, false) :
                convertView;

        com.example.eventhub_jigsaw.Profiles profiles = getItem(position);
        TextView Uname = view.findViewById(R.id.username_profile_text);
        TextView EmailU = view.findViewById(R.id.email_profiles_text);

        if (profiles != null) {
            Uname.setText(profiles.getUsername());
            EmailU.setText(profiles.getEmail());
        }

        return view;
    }
}
