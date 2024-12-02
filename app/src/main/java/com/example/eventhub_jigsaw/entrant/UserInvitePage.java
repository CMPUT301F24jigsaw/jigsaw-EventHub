package com.example.eventhub_jigsaw.entrant;

/**
 * Data class representing an event invitation.
 */
public class UserInvitePage {
    private String eventName_user;
    private int eventImage_user;

    /**
     * Constructs a new UserInvitePage object with the given event details.
     * @param eventName_user The name of the event.
     * @param eventImage_user The image ID associated with the event.
     */
    public UserInvitePage(String eventName_user, int eventImage_user) {
        this.eventName_user = eventName_user;
        this.eventImage_user = eventImage_user;
    }

    /**
     * Gets the name of the event.
     * @return The event name.
     */
    public String getEventName_user() {return eventName_user;}

    /**
     * Gets the image ID for the event.
     * @return The event image ID.
     */
    public int getEventImage_user() {return eventImage_user;}

}
