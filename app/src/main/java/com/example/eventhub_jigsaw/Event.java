package com.example.eventhub_jigsaw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event in the EventHub app, containing an event name and image.
 */
public class Event implements Serializable {
    private String eventName; // Name of event
    private List<User> attendees;
    private String organizerID;
    private String eventDate;
    private int maxAttendees;
    private String description;
    private List<String> waitingList;
    private List<String> sampledUsers;
    private List<String> registeredUsers;
    private List<String> declinedInvitationUser;
    private boolean geolocation;
    private String facilityId; // Add this field for facility ID
    /** The image resource ID for the event */
    private String eventImageUrl; // The image resource Id for the event

    /**
     * Gets the name of the event.
     * @return The event name.
     */
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<User> attendees) {
        this.attendees = attendees;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<String> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return this.eventImageUrl;
    }

    public void setId(String newId) {
        if (newId != "") {
            eventImageUrl = newId;
        }
    }

    /**
     * Constructs a new Events instance with the specified event name and image id
     *
     * @param eventName The name of the event.
     * @param eventDate The date of the event.
     * @param description The details regarding the event.
     * @param eventImage The image resource ID associated with the event.
     */

    public Event() {

    }

    public Event(String eventName, String eventDate, String organizerID, int maxAttendees, String description) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.organizerID = organizerID;
        this.maxAttendees = maxAttendees;
        this.description = description;
        this.waitingList = new ArrayList<>();
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }

    public List<String> getSampledUsers() {
        return sampledUsers;
    }

    public void setSampledUsers(List<String> sampledUsers) {
        this.sampledUsers = sampledUsers;
    }

    public Event(String eventName, String eventDate, String description) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.description = description;
    }

    public List<String> getDeclinedInvitationUser() {
        return declinedInvitationUser;
    }

    public void setDeclinedInvitationUser(List<String> declinedInvitationUser) {
        this.declinedInvitationUser = declinedInvitationUser;
    }

    public boolean isGeolocation() {
        return geolocation;
    }

    public void setGeolocation(boolean geolocation) {
        this.geolocation = geolocation;
    }

    // Getter and Setter for facilityId
    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * Gets the image resource ID for the event.
     *
     * @return The image resource ID associated with the event.
     */
    public int getEventImage() {
        // to be implemented
        return 1;
    }
}

