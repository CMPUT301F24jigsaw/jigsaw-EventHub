package com.example.eventhub_jigsaw.organizer.facility;

// This is a model class representing a single facility
public class OrganizerFacilityPage {
    private String facilityName_organizer;

    // Constructor
    public OrganizerFacilityPage(String facilityName_organizer) {
        this.facilityName_organizer = facilityName_organizer;

    }

    // Getter for facility name
    public String getFacilityName_organizer() {
        return facilityName_organizer;
    }
}
