package com.example.eventhub_jigsaw.organizer.facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;

import com.example.eventhub_jigsaw.R;
import com.example.eventhub_jigsaw.organizer.event.OrganizerEventInfo;
import com.example.eventhub_jigsaw.organizer.event.OrganizerEventPage;

import java.util.List;

public class OrganizerFacilityAdapter extends RecyclerView.Adapter<OrganizerFacilityAdapter.FacilityViewHolder> {

    private List<OrganizerFacilityPage> facilityList;
    private FragmentManager fragmentManager;

    // Constructor to pass event list and FragmentManager
    public OrganizerFacilityAdapter(List<OrganizerFacilityPage> facilityList, FragmentManager fragmentManager) {
        this.facilityList = facilityList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each event item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizers_facilities_page, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        OrganizerFacilityPage event = facilityList.get(position);
        holder.facilityName.setText(event.getEventName_organizer());

        holder.MoreInfo.setOnClickListener(v -> {
            // Create a new DialogFragment instance
            OrganizerEventInfo infoFragment = new OrganizerEventInfo();

            // Pass data to the DialogFragment
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getEventName_organizer());
            infoFragment.setArguments(bundle);

            // Show the DialogFragment
            infoFragment.show(fragmentManager, "event_info_dialog");
        });

//        holder.SampleUser.setOnClickListener(v -> {
//            OrganizerSampleEntrant sampleUsersDialog = new OrganizerSampleEntrant();
//            Bundle bundle = new Bundle();
//            bundle.putString("event_id", event.getEventId()); // Pass event ID
//            sampleUsersDialog.setArguments(bundle);
//            sampleUsersDialog.show(fragmentManager, "sample_users_dialog");
//
//        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of events
        return facilityList.size();
    }

    // ViewHolder class to represent individual event items
    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        Button MoreInfo;
        TextView facilityName;
        Button SampleUser;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            MoreInfo = itemView.findViewById(R.id.MoreInfo_organizer);
            facilityName = itemView.findViewById(R.id.facility_name);
        }
    }
}
