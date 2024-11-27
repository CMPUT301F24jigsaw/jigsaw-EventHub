package com.example.eventhub_jigsaw.admin;

/**
 * Represents a user profile in the EventHub app, containing
 * basic information such as username and email.
 */
public class Profiles {
    /** The username associated with the profile */
    private String username;

    /** The email address associated with the profile. */
    private String email;

    /**
     * Constructs a new instance with the specified username and email.
     *
     * @param username The username of the profile.
     * @param email The email address of the profile.
     */
    public Profiles(String username, String email) {
        this.username = username;
        this.email = email;
    }

    /**
     * Gets the username associated with the profile.
     *
     * @return The username of the profile..
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with this profile.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address associated with this profile.
     *
     * @return The email address of the profile.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address associated with this profile.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
