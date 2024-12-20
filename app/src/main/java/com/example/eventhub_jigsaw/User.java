package com.example.eventhub_jigsaw;

import com.example.eventhub_jigsaw.admin.Events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a user in the event hub.
 * Can be an Entrant or an Organizer.
 */

public class User implements Serializable {

    public enum Role {
        ENTRANT, ORGANIZER
    }

    private String name;
    private String email;
    private String userID;
    private long phone;
    private Boolean adminNotification;
    private Boolean organizerNotification;
    private Role role; // Role of the user (ENTRANT or ORGANIZER)
    private String profileImageUrl; // URL for the profile image
    private List<String> WaitingList;
    private List<String> eventAcceptedByOrganizer;
    private List<String> registeredEvents;
    private List<String> notifications;
    private double latitude;
    private double longitude;
    private Boolean isAdmin;


    /**
     * Constructor to initialize the user with basic details.
     *
     * @param name Name of the user
     * @param email Email address of the user
     * @param userID Unique identifier for the user
     * @param phone Phone number of the user
     * @param role Role (ENTRANT or ORGANIZER)
     */
    public User(String name, String email, String userID, long phone, Role role) {
        this.name = name;
        this.email = email;
        this.userID = userID;
        this.phone = phone;
        this.role = role;
        this.adminNotification = false;
        this.organizerNotification = true;
        this.WaitingList = new ArrayList<>();
    }

    // Getters and setters for user attributes

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public Boolean getAdminNotification() {
        return adminNotification;
    }

    public void setAdminNotification(Boolean adminNotification) {
        this.adminNotification = adminNotification;
    }

    public Boolean getOrganizerNotification() {
        return organizerNotification;
    }

    public void setOrganizerNotification(Boolean organizerNotification) {
        this.organizerNotification = organizerNotification;
    }

    public Role getRole() {
        return role;
    }

    public List<String> getWaitingList() {
        return WaitingList;
    }

    public List<String> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(List<String> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }

    public void setWaitingList(List<String> waitingList) {
        WaitingList = waitingList;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<String> getEventAcceptedByOrganizer() {
        return eventAcceptedByOrganizer;
    }

    public void setEventAcceptedByOrganizer(List<String> eventAcceptedByOrganizer) {
        this.eventAcceptedByOrganizer = eventAcceptedByOrganizer;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
