package com.example.eventhub_jigsaw;
import static com.example.eventhub_jigsaw.User.Role.ENTRANT;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import android.graphics.Bitmap;

import com.example.eventhub_jigsaw.admin.Events;

import java.util.ArrayList;
import java.util.List;



public class EventTest {
    @Test
    void testGetEventName() {
        String expectedEventName = "blobEvent";
        Events myEvent = new Events(expectedEventName, 0);
        String resultEventName = myEvent.getEventName();
        assertEquals(resultEventName, expectedEventName);
    }

    @Test
    void testGetEventImage() {
        int expectedEventImageId = 013;
        Events myEvent = new Events("Event 1", expectedEventImageId);
        int resultEventImageId = myEvent.getEventImage();
        assertEquals(resultEventImageId, expectedEventImageId);
    }

    @Test
    void testDefaultConstructor() {
        Event event = new Event();
        assertNull(event.getEventName());
        assertNull(event.getEventDate());
        assertNull(event.getDescription());
    }

    @Test
    void testParameterizedConstructor() {
        Event event = new Event("1", "Conference", "2024-12-05", "123", 100, "Tech Conference");
        assertEquals("Conference", event.getEventName());
        assertEquals("2024-12-05", event.getEventDate());
        assertEquals("Tech Conference", event.getDescription());
        assertEquals(100, event.getMaxAttendees());
    }

    @Test
    void testSetAndGetEventName() {
        Event event = new Event();
        event.setEventName("Workshop");
        assertEquals("Workshop", event.getEventName());
    }

    @Test
    void testSetAndGetEventDate() {
        Event event = new Event();
        event.setEventDate("2024-12-10");
        assertEquals("2024-12-10", event.getEventDate());
    }

    @Test
    void testSetAndGetDescription() {
        Event event = new Event();
        event.setDescription("This is a description.");
        assertEquals("This is a description.", event.getDescription());
    }

    @Test
    void testSetAndGetAttendees() {
        Event event = new Event();
        List<User> attendees = new ArrayList<>();
        User user = new User("User1", "user1@gmail.zo", "mockUserId001", 428904, ENTRANT);
        attendees.add(user);
        event.setAttendees(attendees);
        assertEquals(attendees, event.getAttendees());
    }

    @Test
    void testSetAndGetWaitingList() {
        Event event = new Event();
        List<String> waitingList = new ArrayList<>();
        waitingList.add("User1");
        event.setWaitingList(waitingList);
        assertEquals(waitingList, event.getWaitingList());
    }

    @Test
    void testSetAndGetGeolocation() {
        Event event = new Event();
        event.setGeolocation(true);
        assertTrue(event.isGeolocation());
    }

    @Test
    void testSetAndGetFacilityId() {
        Event event = new Event();
        event.setFacilityId("Facility123");
        assertEquals("Facility123", event.getFacilityId());
    }

    @Test
    void testSetAndGetEventID() {
        Event event = new Event();
        event.setEventID("E123");
        assertEquals("E123", event.getEventID());
    }

    @Test
    void testSetAndGetImageID() {
        Event event = new Event();
        event.setImageID("IMG123");
        assertEquals("IMG123", event.getImageID());
    }

    @Test
    void testWaitingListLimit() {
        Event event = new Event();
        event.setWaitingListLimit(50);
        assertEquals(50, event.getWaitingListLimit());
    }
}
