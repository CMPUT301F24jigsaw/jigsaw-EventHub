package com.example.eventhub_jigsaw.organizer;

// This is a model class representing a single event
public class OrganizerEventPage {
    private String eventName_organizer;
    private int eventImage_organizer;

    // Constructor
    public OrganizerEventPage(String eventName_organizer, int eventImage_organizer) {
        this.eventName_organizer = eventName_organizer;
        this.eventImage_organizer = eventImage_organizer;
    }

    // Getter for event name
    public String getEventName_organizer() {
        return eventName_organizer;
    }

    // Getter for event image
    public int getEventImage_organizer() {
        return eventImage_organizer;
    }
}
