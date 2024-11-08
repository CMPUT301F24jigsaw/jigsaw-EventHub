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
/**
 * ProfileArrayAdapter is a custom adapter for displaying profile data in a list view.
 * It maps a list of Profiles objects to a view layout.
 */

public class ProfileArrayAdapter extends ArrayAdapter<Profiles> {

    /**
     * Constructor for the ProfileArrayAdapter.
     *
     * @param context The current context.
     * @param profiles The list of Profiles objects to display.
     */
    public ProfileArrayAdapter(Context context, ArrayList<Profiles> cities) {
        super(context, 0, cities);
    }
    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
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
