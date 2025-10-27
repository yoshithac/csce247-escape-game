package com.model;

import java.time.LocalDateTime;

/**
 * User entity for authentication and user management
 * Stores user credentials, profile info, and timestamps
 */
public class User {
    private String userId;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;  // USER or ADMIN
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
    public User() {
        this.createdAt = LocalDateTime.now();
        this.role = "USER";
    }
    
<<<<<<< HEAD
=======
    /**
     * Constructs a new {@code User} with basic registration information.
     *
     * @param userId the user's unique identifier or username
     * @param password the user's password
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     */
>>>>>>> main
    public User(String userId, String password, String firstName, String lastName, String email) {
        this();
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
<<<<<<< HEAD
    }
    
    // Getters
    public String getUserId() { 
        return userId; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public String getFirstName() { 
        return firstName; 
    }
    
    public String getLastName() { 
        return lastName; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getRole() { 
        return role; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
=======
    }
    
    // Getters

    /**
     * Returns the unique user ID.
     *
     * @return the user's ID
     */
    public String getUserId() { 
        return userId; 
    }

    /**
     * Returns the user's password.
     *
     * @return the password
     */
    public String getPassword() { 
        return password; 
    }

    /**
     * Returns the user's first name.
     *
     * @return the first name
     */
    public String getFirstName() { 
        return firstName; 
    }

    /**
     * Returns the user's last name.
     *
     * @return the last name
     */
    public String getLastName() { 
        return lastName; 
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address
     */
    public String getEmail() { 
        return email; 
    }

    /**
     * Returns the user's role.
     *
     * @return the role (e.g., USER or ADMIN)
     */
    public String getRole() { 
        return role; 
    }

    /**
     * Returns the timestamp when the account was created.
     *
     * @return the account creation timestamp
     */
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    /**
     * Returns the timestamp of the user's last login.
     *
     * @return the last login timestamp, or {@code null} if never logged in
     */
>>>>>>> main
    public LocalDateTime getLastLoginAt() { 
        return lastLoginAt; 
    }
    
    // Setters
<<<<<<< HEAD
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }
    
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
=======

    /**
     * Sets the user ID.
     *
     * @param userId the new user ID
     */
    public void setUserId(String userId) { 
        this.userId = userId; 
    }

    /**
     * Sets the user's password.
     *
     * @param password the new password
     */
    public void setPassword(String password) { 
        this.password = password; 
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    /**
     * Sets the user's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) { 
        this.email = email; 
    }

    /**
     * Sets the user's role (e.g., USER or ADMIN).
     *
     * @param role the new role
     */
    public void setRole(String role) { 
        this.role = role; 
    }

    /**
     * Sets the timestamp of account creation.
     *
     * @param createdAt the creation time
     */
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    /**
     * Sets the timestamp of the last login.
     *
     * @param lastLoginAt the last login time
     */
>>>>>>> main
    public void setLastLoginAt(LocalDateTime lastLoginAt) { 
        this.lastLoginAt = lastLoginAt; 
    }
    
    // Utility methods
<<<<<<< HEAD
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }
    
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
=======

    /**
     * Checks whether this user has an administrator role.
     *
     * @return {@code true} if the role is ADMIN, otherwise {@code false}
     */
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    /**
     * Updates the {@code lastLoginAt} field to the current system time.
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * Returns the user's full name (first + last).
     *
     * @return the user's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns a formatted string representation of the user.
     * Format: {@code User[userId: firstName lastName (email)]}.
     * @return a string describing the user
     */
>>>>>>> main
    @Override
    public String toString() {
        return String.format("User[%s: %s %s (%s)]", userId, firstName, lastName, email);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> main
