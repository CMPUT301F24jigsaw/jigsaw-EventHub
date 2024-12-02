package com.example.eventhub_jigsaw.admin;

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
