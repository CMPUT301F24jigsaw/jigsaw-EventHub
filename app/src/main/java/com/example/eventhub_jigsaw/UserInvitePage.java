package com.example.eventhub_jigsaw;

public class UserInvitePage {
    private String eventName_user;
    private int eventImage_user;

    public UserInvitePage(String eventName_user, int eventImage_user) {
        this.eventName_user = eventName_user;
        this.eventImage_user = eventImage_user;
    }

    public String getEventName_user() {return eventName_user;}

    public int getEventImage_user() {return eventImage_user;}

}
