package com.example.eventhub_jigsaw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class FacilityTest {

    @Test
    void testDefaultConstructor() {
        Facility facility = new Facility();
        assertNotNull(facility);
    }

    @Test
    void testParameterizedConstructor() {
        Facility facility = new Facility("Conference Room A", "O123", "New York", 100);
        assertEquals("Conference Room A", facility.getName());
        assertEquals("O123", facility.getOrganizerID());
        assertEquals("New York", facility.getLocation());
        assertEquals(100, facility.getCapacity());
        assertNotNull(facility.getEventIds());
        assertTrue(facility.getEventIds().isEmpty());
    }

    @Test
    void testInvalidOrganizerIDInConstructor() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Facility("Conference Room A", "", "New York", 100);
        });
        assertEquals("Organizer ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testSetName() {
        Facility facility = new Facility();
        facility.setName("Auditorium");
        assertEquals("Auditorium", facility.getName());
    }

    @Test
    void testSetInvalidName() {
        Facility facility = new Facility();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facility.setName("");
        });
        assertEquals("Name cannot be Empty", exception.getMessage());
    }

    @Test
    void testSetLocation() {
        Facility facility = new Facility();
        facility.setLocation("Los Angeles");
        assertEquals("Los Angeles", facility.getLocation());
    }

    @Test
    void testSetInvalidLocation() {
        Facility facility = new Facility();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facility.setLocation("");
        });
        assertEquals("Location cannot be empty", exception.getMessage());
    }

    @Test
    void testSetCapacity() {
        Facility facility = new Facility();
        facility.setCapacity(150);
        assertEquals(150, facility.getCapacity());
    }

    @Test
    void testSetInvalidCapacity() {
        Facility facility = new Facility();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facility.setCapacity(0);
        });
        assertEquals("Capacity must be greater than 0.", exception.getMessage());
    }

    @Test
    void testAddEvent() {
        Facility facility = new Facility();
        facility.addEvent("E123");
        assertTrue(facility.getEventIds().contains("E123"));
    }

    @Test
    void testAddDuplicateEvent() {
        Facility facility = new Facility();
        facility.addEvent("E123");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facility.addEvent("E123");
        });
        assertEquals("Event ID cannot be null, empty, or duplicate.", exception.getMessage());
    }

    @Test
    void testRemoveEvent() {
        Facility facility = new Facility();
        facility.addEvent("E123");
        facility.removeEvent("E123");
        assertFalse(facility.getEventIds().contains("E123"));
    }

    @Test
    void testRemoveInvalidEvent() {
        Facility facility = new Facility();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facility.removeEvent("");
        });
        assertEquals("Event ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testHasEvent() {
        Facility facility = new Facility();
        facility.addEvent("E123");
        assertTrue(facility.hasEvent("E123"));
        assertFalse(facility.hasEvent("E999"));
    }

    @Test
    void testCheckFields() {
        Facility facility = new Facility("Auditorium", "O123", "San Francisco", 200);
        facility.setId("F123");
        assertTrue(facility.checkFields());
    }

    @Test
    void testCheckFieldsWithInvalidData() {
        Facility facility = new Facility();
        assertFalse(facility.checkFields());
    }

    @Test
    void testToString() {
        Facility facility = new Facility("Auditorium", "O123", "San Francisco", 200);
        assertEquals("Auditorium", facility.toString());
    }

    @Test
    void testSetEventIds() {
        Facility facility = new Facility();
        List<String> eventIds = new ArrayList<>();
        eventIds.add("E123");
        facility.setEventIds(eventIds);
        assertEquals(eventIds, facility.getEventIds());
    }

    @Test
    void testSetOrganizerID() {
        Facility facility = new Facility();
        facility.setOrganizerID("O123");
        assertEquals("O123", facility.getOrganizerID());
    }

    @Test
    void testSetFacilityID() {
        Facility facility = new Facility();
        facility.setFacilityID("F123");
        assertEquals("F123", facility.getFacilityID());
    }
}
