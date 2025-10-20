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
    /**
     * Setter for userId
     * @param userId unique ID of the user
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Setter for password
     * @param password password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Setter for firstName
     * @param firstName first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter for lastName
     * @param lastName last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Setter for email
     * @param email email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter for role
     * @param role role of the user
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Setter for penaltyPoints
     * @param penaltyPoints number of penalty points
     */
    public void setPenaltyPoints(int penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

    /**
     * Setter for scorePoints
     * @param scorePoints number of score points
     */
    public void setScorePoints(int scorePoints) {
        this.scorePoints = scorePoints;
    }
    
    /**
     * Adds score points to the user's total
     * @param points number of points to add
     */
    public void addScorePoints(int points) {
        if (points > 0) {
            this.scorePoints += points;
        }
    }

    /**
     * Adds penalty points to the user's total
     * @param points number of penalty points to add
     */
    public void addPenaltyPoints(int points) {
        if (points > 0) {
            this.penaltyPoints += points;
        }
    }

    /**
     * Checks if the user has an admin role
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("admin");
    }

    /**
     * Compares two users based on scorePoints for leaderboard ranking
     * @param other the other user to compare to
     * @return comparison result (descending by score, then userId)
     */
    public int compareTo(User other) {
        int scoreCompare = Integer.compare(other.scorePoints, this.scorePoints);
        if (scoreCompare != 0) return scoreCompare;
        return this.userId.compareToIgnoreCase(other.userId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(userId);        
        sb.append(", firstName=").append(firstName);
        sb.append(", lastName=").append(lastName);
        sb.append(", email=").append(email);        
        
        return sb.toString();
    }

}