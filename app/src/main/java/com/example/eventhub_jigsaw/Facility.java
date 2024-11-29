package com.example.eventhub_jigsaw;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Facility implements Serializable {
    private String id;
    private String facilityname;
    private String facilitylocation;
    private int capacity;
    private List<String> eventIDs; // List of event IDs hosted in this building

    // Constructor
    public Facility(String name, String location, int capacity) {
        this.facilityname = name;
        this.facilitylocation = location;
        this.capacity = capacity;
    }

    // Default Constructor
    public Facility() {
        this.eventIDs = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return facilityname;
    }

    public void setName(String name) {
        this.facilityname = name;
    }

    public String getLocation() {
        return facilitylocation;
    }

    public void setLocation(String location) {
        this.facilitylocation = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getEventIDs() {
        return eventIDs;
    }

    public void addEvent(String eventID) {
        this.eventIDs.add(eventID);
    }

    public void removeEvent(String eventID) {
        this.eventIDs.remove(eventID);
    }

    // Utility Method: Check if a specific event is hosted in this building
    public boolean hasEvent(String eventID) {
        return this.eventIDs.contains(eventID);
    }


    public boolean checkFields() {
        // Check for null or empty string fields
        if (id == null || id.trim().isEmpty()) return false;
        if (facilityname == null || facilityname.trim().isEmpty()) return false;
        if (facilitylocation == null || facilitylocation.trim().isEmpty()) return false;

        // Check for invalid capacity (e.g., negative or zero)
        if (capacity <= 0) return false;

        // All fields are valid
        return true;
    }



}
