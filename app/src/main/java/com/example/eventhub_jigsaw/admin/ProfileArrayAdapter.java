package com.example.eventhub_jigsaw.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventhub_jigsaw.R;

import java.util.ArrayList;

/**
 * ProfileArrayAdapter displays Profiles objects in a ListView.
 * Converts Profiles objects into list items for display purposes.
 */


public class ProfileArrayAdapter extends ArrayAdapter<Profiles> {

    public ProfileArrayAdapter(Context context, ArrayList<Profiles> cities) {
        super(context, 0, cities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null) ?
                LayoutInflater.from(getContext()).inflate(R.layout.profiles_content, parent, false) :
                convertView;

        Profiles profiles = getItem(position);
        TextView Uname = view.findViewById(R.id.username_profile_text);
        TextView EmailU = view.findViewById(R.id.email_profiles_text);

        if (profiles != null) {
            Uname.setText(profiles.getUsername());
            EmailU.setText(profiles.getEmail());
        }

        return view;
    }
}
