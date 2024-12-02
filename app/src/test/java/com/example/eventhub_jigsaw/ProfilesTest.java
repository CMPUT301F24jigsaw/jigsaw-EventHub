package com.example.eventhub_jigsaw;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.example.eventhub_jigsaw.admin.Profiles;

/**
 * Test suite for the Profiles class.
 */
class ProfilesTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange: Create a new Profiles instance
        String username = "testUser";
        String email = "test@example.com";
        Profiles profile = new Profiles(username, email);

        // Act & Assert: Verify constructor and getters
        assertEquals(username, profile.getUsername(), "Username should match the value passed to the constructor");
        assertEquals(email, profile.getEmail(), "Email should match the value passed to the constructor");
    }

    @Test
    void testSetUsername() {
        // Arrange: Create a new Profiles instance
        Profiles profile = new Profiles("initialUser", "test@example.com");

        // Act: Set a new username
        String newUsername = "updatedUser";
        profile.setUsername(newUsername);

        // Assert: Verify the username was updated
        assertEquals(newUsername, profile.getUsername(), "Username should be updated correctly");
    }

    @Test
    void testSetEmail() {
        // Arrange: Create a new Profiles instance
        Profiles profile = new Profiles("testUser", "initial@example.com");

        // Act: Set a new email
        String newEmail = "updated@example.com";
        profile.setEmail(newEmail);

        // Assert: Verify the email was updated
        assertEquals(newEmail, profile.getEmail(), "Email should be updated correctly");
    }
}
