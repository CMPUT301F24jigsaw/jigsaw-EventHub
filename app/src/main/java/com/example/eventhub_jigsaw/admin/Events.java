package com.example.eventhub_jigsaw.admin;

/**
 * Represents an event in the EventHub app, containing an event name and image.
 */
public class Events {
    /** The name of the event */
    private String eventName;

    /** The image resource ID for the event */
    private int eventImage;

    /**
     * Constructs a new Events instance with the specified event name and image id
     *
     * @param eventName The name of the event.
     * @param eventImage The image resource ID associated with the event.
     */
    public Events(String eventName, int eventImage) {
        this.eventName = eventName;
        this.eventImage = eventImage;
    }

    /**
     * Gets the name of the event.
     * @return The event name.
     */
    public String getEventName() {
        return eventName;
    }


    /**
     * Gets the image resource ID for the event.
     *
     * @return The image resource ID associated with the event.
     */
    public int getEventImage() {
        return eventImage;
    }
}
