package com.example.eventhub_jigsaw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructor() {
        User user = new User("John Doe", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("U123", user.getUserID());
        assertEquals(1234567890L, user.getPhone());
        assertEquals(User.Role.ENTRANT, user.getRole());
        assertFalse(user.getAdminNotification());
        assertTrue(user.getOrganizerNotification());
        assertNotNull(user.getWaitingList());
    }

    @Test
    void testSetAndGetName() {
        User user = new User("Jane", "jane@example.com", "U456", 9876543210L, User.Role.ORGANIZER);
        user.setName("Jane Doe");
        assertEquals("Jane Doe", user.getName());
    }

    @Test
    void testSetAndGetEmail() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testSetAndGetUserID() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setUserID("U999");
        assertEquals("U999", user.getUserID());
    }

    @Test
    void testSetAndGetPhone() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setPhone(1122334455L);
        assertEquals(1122334455L, user.getPhone());
    }

    @Test
    void testSetAndGetRole() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setRole(User.Role.ORGANIZER);
        assertEquals(User.Role.ORGANIZER, user.getRole());
    }

    @Test
    void testSetAndGetAdminNotification() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setAdminNotification(true);
        assertTrue(user.getAdminNotification());
    }

    @Test
    void testSetAndGetOrganizerNotification() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setOrganizerNotification(false);
        assertFalse(user.getOrganizerNotification());
    }

    @Test
    void testSetAndGetWaitingList() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        List<String> waitingList = new ArrayList<>();
        waitingList.add("Event1");
        user.setWaitingList(waitingList);
        assertEquals(waitingList, user.getWaitingList());
    }

    @Test
    void testSetAndGetRegisteredEvents() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        List<String> registeredEvents = new ArrayList<>();
        registeredEvents.add("Event1");
        user.setRegisteredEvents(registeredEvents);
        assertEquals(registeredEvents, user.getRegisteredEvents());
    }

    @Test
    void testSetAndGetProfileImageUrl() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setProfileImageUrl("http://example.com/profile.jpg");
        assertEquals("http://example.com/profile.jpg", user.getProfileImageUrl());
    }

    @Test
    void testSetAndGetLatitude() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setLatitude(37.7749);
        assertEquals(37.7749, user.getLatitude());
    }

    @Test
    void testSetAndGetLongitude() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setLongitude(-122.4194);
        assertEquals(-122.4194, user.getLongitude());
    }

    @Test
    void testSetAndGetIsAdmin() {
        User user = new User("John", "john@example.com", "U123", 1234567890L, User.Role.ENTRANT);
        user.setAdmin(true);
        assertTrue(user.getAdmin());
    }
}
