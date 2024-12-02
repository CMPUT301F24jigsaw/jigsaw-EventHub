package com.example.eventhub_jigsaw.admin;

/**
 * Events is a data model class representing an event.
 * It includes the event's name and associated image resource.
 */

public class Events {
    private String eventName;
    private int eventImage;

    public Events(String eventName, int eventImage) {
        this.eventName = eventName;
        this.eventImage = eventImage;
    }

    public String getEventName() {
        return eventName;
    }


    public int getEventImage() {
        return eventImage;
    }
}
