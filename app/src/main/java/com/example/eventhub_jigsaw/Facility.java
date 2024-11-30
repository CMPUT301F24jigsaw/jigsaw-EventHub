package com.example.eventhub_jigsaw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Facility {
    private String id; // Unique identifier for the facility
    private String name; // Facility name
    private String location; // Facility location
    private int capacity; // Maximum capacity of the facility
    private List<String> eventIds; // List of event IDs hosted in this facility
    private String organizerID; // Organizer ID
    private String facilityID;

    // No-argument constructor required by Firestore
    public Facility() {
        // Firestore needs a no-argument constructor for deserialization
    }

    public Facility(String name, String organizerID, String location, int maxAttendees) {
        if (organizerID == null || organizerID.trim().isEmpty()) {
            throw new IllegalArgumentException("Organizer ID cannot be null or empty.");
        }

        this.organizerID = organizerID.trim();  // Initialize final field
        this.name = name != null ? name.trim() : null;
        this.location = location != null ? location.trim() : null;
        this.capacity = maxAttendees;
        this.eventIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.trim() : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != "") {
            this.name = name.trim();
        } else {
            throw new IllegalArgumentException("Name cannot be Empty");
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location != "") {
            this.location = location.trim();
        } else {
            throw new IllegalArgumentException("Location cannot be empty");
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        } else {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
    }

    public List<String> getEventIds() {
        return new ArrayList<>(eventIds); // Return a copy for safety
    }

    // Add an event ID to the facility's event list
    public void addEvent(String eventId) {
        if (eventId != null && !eventId.trim().isEmpty() && !eventIds.contains(eventId.trim())) {
            eventIds.add(eventId.trim());
        } else {
            throw new IllegalArgumentException("Event ID cannot be null, empty, or duplicate.");
        }
    }

    // Remove an event ID from the facility's event list
    public void removeEvent(String eventId) {
        if (eventId != null && !eventId.trim().isEmpty()) {
            eventIds.remove(eventId.trim());
        } else {
            throw new IllegalArgumentException("Event ID cannot be null or empty.");
        }
    }

    // Check if a specific event is hosted in this facility
    public boolean hasEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID cannot be null or empty.");
        }
        return eventIds.contains(eventId.trim());
    }

    // Validate facility fields
    public boolean checkFields() {
        return id != null && !id.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                location != null && !location.trim().isEmpty() &&
                capacity > 0 &&
                organizerID != null && !organizerID.trim().isEmpty();
    }

    @Override
    public String toString() {
        return name; // Return the facility name for display
    }

    public void setEventIds(List<String> eventIds) {
        this.eventIds = eventIds;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public Facility(String facilityID, String name) {
        this.facilityID = facilityID;
        this.name = name;
    }
}
