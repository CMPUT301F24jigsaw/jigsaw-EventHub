package com.example.eventhub_jigsaw.organizer.facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhub_jigsaw.R;

import java.util.List;

public class OrganizerFacilityAdapter extends RecyclerView.Adapter<OrganizerFacilityAdapter.FacilityViewHolder> {

    private List<OrganizerFacilityPage> facilityList;
    private FragmentManager fragmentManager;

    // Constructor to pass the facility list
    public OrganizerFacilityAdapter(List<OrganizerFacilityPage> facilityList, FragmentManager fragmentManager) {
        this.facilityList = facilityList;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each facility item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_facility_items, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        OrganizerFacilityPage facility = facilityList.get(position);
        holder.facilityName.setText(facility.getFacilityName_organizer());
    }

    @Override
    public int getItemCount() {
        // Return the total number of facilities
        return facilityList.size();
    }

    // ViewHolder class to represent individual facility items
    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the TextView
            facilityName = itemView.findViewById(R.id.facility_name);
        }
    }
}
