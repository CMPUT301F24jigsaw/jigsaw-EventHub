package com.example.eventhub_jigsaw.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventhub_jigsaw.Facility;
import com.example.eventhub_jigsaw.R;

import java.util.ArrayList;

/**
 * FacilityArrayAdapter displays Facility objects in a ListView.
 * Converts Facility objects into list items for user interaction.
 */

public class FacilityArrayAdapter extends ArrayAdapter<Facility> {
    private ArrayList<Facility> facilities;
    private Context context;

    public FacilityArrayAdapter(Context context, ArrayList<Facility> facilities){
        super(context,0, facilities);
        this.facilities = facilities;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.facilities_item, parent,false);
        }

        Facility facility = facilities.get(position);

        TextView facilityName = view.findViewById(R.id.facility_text);
        TextView facilityLocation = view.findViewById(R.id.location_text);
        TextView facilityCapacity = view.findViewById(R.id.capacity_text);

        facilityName.setText(facility.getName() != null ? facility.getName() : "N/A");
        facilityLocation.setText(facility.getLocation() );
        facilityCapacity.setText(String.valueOf(facility.getCapacity()));

        return view;
    }
}