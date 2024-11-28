package com.example.eventhub_jigsaw;

import com.example.eventhub_jigsaw.admin.Events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    public enum Role {
        ENTRANT, ORGANIZER
    }

    private String name;
    private String email;
    private String userID;
    private int phone;
    private Boolean adminNotification;
    private Boolean organizerNotification;
    private Role role; // Role of the user (ENTRANT or ORGANIZER)
    private List<Events> WaitingList;

    // Constructor for general users
    public User(String name, String email, String userID, int phone, Role role) {
        this.name = name;
        this.email = email;
        this.userID = userID;
        this.phone = phone;
        this.role = role;
        this.adminNotification = false;
        this.organizerNotification = false;
        this.WaitingList = new ArrayList<>();
    }

    // Getters and setters
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

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
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

    public List<Events> getWaitingList() {
        return WaitingList;
    }

    public void setWaitingList(List<Events> waitingList) {
        WaitingList = waitingList;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
