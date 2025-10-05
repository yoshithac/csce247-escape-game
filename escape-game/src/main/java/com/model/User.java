package com.model;

/**
 * Represents a user in the escape game
 * @author We're Getting an A
 */
public class User {
    private String userId;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private int penaltyPoints;
    private int scorePoints;

    /**
     * Constructor for User
     */
    public User() {
        this.userId = "";
        this.password = "";
        this.email = "";
        this.firstName = "";
        this.lastName = "";
        this.role = "";
        this.penaltyPoints = 0;
        this.scorePoints = 0;
    }

    /**
     * Getter for userId
     * @return userId
     */
    public String getUserId() {
        return userId;
    }
}