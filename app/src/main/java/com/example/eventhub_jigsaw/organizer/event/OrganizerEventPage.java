package com.example.eventhub_jigsaw.organizer.event;

import android.graphics.Bitmap;

import java.io.Serializable;

// This is a model class representing a single event
public class OrganizerEventPage implements Serializable {

    private String eventName_organizer;
    private Bitmap eventImageBitmap_organizer;

    // Constructor with Bitmap for event image
    public OrganizerEventPage(String eventName_organizer, Bitmap eventImageBitmap_organizer) {
        this.eventName_organizer = eventName_organizer;
        this.eventImageBitmap_organizer = eventImageBitmap_organizer;
    }

    public void setEventName_organizer(String eventName_organizer) {
        this.eventName_organizer = eventName_organizer;
    }
    // Getter for event name
    public String getEventName_organizer() {
        return eventName_organizer;
    }

    // Getter for event image Bitmap
    public Bitmap getEventImageBitmap_organizer() {
        return eventImageBitmap_organizer;
    }

    public void setEventImageBitmap_organizer(Bitmap eventImageBitmap_organizer) {
        this.eventImageBitmap_organizer = this.eventImageBitmap_organizer;
    }
}
