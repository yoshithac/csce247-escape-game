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
    
    /** 
     * Default constructor
     */
    public User() {
        this.createdAt = LocalDateTime.now();
        this.role = "USER";
    }
    
    /** 
     * Constructor with all fields
     * @param userId
     * @param password
     * @param firstName
     * @param lastName
     * @param email
     */
    public User(String userId, String password, String firstName, String lastName, String email) {
        this();
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters
    /**
     * Get user ID  
     * @return String userId
     */
    public String getUserId() { 
        return userId; 
    }
    
    /**
     * Get password
     * @return String password
     */
    public String getPassword() { 
        return password; 
    }
    
    /**
     * Get first name
     * @return String firstName
     */
    public String getFirstName() { 
        return firstName; 
    }
    
    /**
     * Get last name
     * @return String lastName
     */
    public String getLastName() { 
        return lastName; 
    }
    
    /**
     * Get email
     * @return String email
     */
    public String getEmail() { 
        return email; 
    }
    
    /**
     * Get role
     * @return String role
     */
    public String getRole() { 
        return role; 
    }
    
    /**
     * Get account creation timestamp
     * @return LocalDateTime createdAt
     */
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    /**
     * Get last login timestamp
     * @return LocalDateTime lastLoginAt
     */
    public LocalDateTime getLastLoginAt() { 
        return lastLoginAt; 
    }
    
    // Setters
    /**
     * Set user ID
     * @param userId
     */
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    /**
     * Set password
     * @param password
     */
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    /**
     * Set first name
     * @param firstName
     */
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }
    
    /**
     * Set last name
     * @param lastName
     */
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
    
    /**
     * Set email
     * @param email
     */
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    /**
     * Set role
     * @param role
     */
    public void setRole(String role) { 
        this.role = role; 
    }
    
    /**
     * Set account creation timestamp
     * @param createdAt
     */
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    /**
     * Set last login timestamp
     * @param lastLoginAt
     */
    public void setLastLoginAt(LocalDateTime lastLoginAt) { 
        this.lastLoginAt = lastLoginAt; 
    }
    
    // Utility methods
    /**
     * Check if user has admin role
     * @return boolean isAdmin
     */
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }
    
    /**
     * Update last login timestamp to current time
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
    
    /**
     * Get full name (first + last)
     * @return String fullName
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Override toString for easy debugging
     * @return String representation of User
     * @Override
     */
    public String toString() {
        return String.format("User[%s: %s %s (%s)]", userId, firstName, lastName, email);
    }
}