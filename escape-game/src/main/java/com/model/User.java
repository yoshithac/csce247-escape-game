package com.model;

/**
 * Represents a user in the escape game
 * @author We're Getting an A
 */
public class User {
    private String userId;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private int penaltyPoints;
    private int scorePoints;

    /**
     * Constructor for User
     */
    public User(String userId, String password, String firstName, String lastName, String email, String role, int penaltyPoints, int scorePoints) {
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.penaltyPoints = penaltyPoints;
        this.scorePoints = scorePoints;
    }

    /**
     * Getter for userId
     * @return userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Getter for password
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Getter for firstName
     * @return firstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Getter for lastName
     * @return lastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Getter for email
     * @return email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Getter for role
     * @return role
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Getter for penaltyPoints
     * @return penaltyPoints
     */
    public int getPenaltyPoints() {
        return this.penaltyPoints;
    }

    /**
     * Getter for scorePoints
     * @return scorePoints
     */
    public int getScorePoints() {
        return this.scorePoints;
    }
}