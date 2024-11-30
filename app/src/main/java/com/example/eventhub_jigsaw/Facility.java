package com.example.eventhub_jigsaw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Facility implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization

    private final String organizerID;
    private String id; // Unique identifier for the facility
    private String name; // Facility name
    private String location; // Facility location
    private int capacity; // Maximum capacity of the facility
    private List<String> eventIds; // List of event IDs hosted in this facility

    // Constructor with name, organizer ID, location (String), and capacity
    public Facility(String name, String organizerID, String location, int maxAttendees) {
        this.name = name;
        this.organizerID = organizerID;
        this.location = location;
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
        this.name = name != null ? name.trim() : null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location != null ? location.trim() : null;
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
        if (eventId != null && !eventId.trim().isEmpty()) {
            eventIds.add(eventId.trim());
        } else {
            throw new IllegalArgumentException("Event ID cannot be null or empty.");
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
                capacity > 0;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", eventIds=" + eventIds +
                '}';
    }
}
