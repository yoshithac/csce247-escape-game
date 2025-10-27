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
    
    public User(String userId, String password, String firstName, String lastName, String email) {
        this();
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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
    
    public LocalDateTime getLastLoginAt() { 
        return lastLoginAt; 
    }
    
    // Setters
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
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) { 
        this.lastLoginAt = lastLoginAt; 
    }
    
    // Utility methods
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }
    
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return String.format("User[%s: %s %s (%s)]", userId, firstName, lastName, email);
    }
}
