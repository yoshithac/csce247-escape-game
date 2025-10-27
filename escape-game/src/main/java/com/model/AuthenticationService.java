<<<<<<< HEAD
package com.model;

import java.util.Optional;

/**
 * Service for user authentication operations
 * Handles login, registration, logout, and session management
 */
public class AuthenticationService {
    private final GameDataFacade dataFacade;
    private User currentUser;
    
    public AuthenticationService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    /**
     * Authenticate user with userId and password
     * @param userId User's ID
     * @param password User's password
     * @return true if login successful
     */
    public boolean login(String userId, String password) {
        Optional<User> userOpt = dataFacade.getUser(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                currentUser = user;
                currentUser.updateLastLogin();
                dataFacade.updateUser(currentUser);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Register a new user
     * @param userId Unique user ID
     * @param password User's password (min 4 chars)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @return true if registration successful
     */
    public boolean register(String userId, String password, String firstName, 
                           String lastName, String email) {
        // Validation
        if (userId == null || userId.trim().isEmpty()) return false;
        if (userId == null || userId.length() != 5) return false;
        if (password == null || password.length() < 4) return false;
        if (dataFacade.userIdExists(userId)) return false;
        //if (dataFacade.emailExists(email)) return false;
        
        // Create and add new user
        User newUser = new User(userId, password, firstName, lastName, email);
        return dataFacade.addUser(newUser);
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get the currently logged in user
     * @return Current User object or null
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
=======
package com.model;

import java.util.Optional;

/**
 * Service for user authentication operations
 * Handles login, registration, logout, and session management
 */
public class AuthenticationService {
    private final GameDataFacade dataFacade;
    private User currentUser;
    
    public AuthenticationService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    /**
     * Authenticate user with userId and password
     * @param userId User's ID
     * @param password User's password
     * @return true if login successful
     */
    public boolean login(String userId, String password) {
        Optional<User> userOpt = dataFacade.getUser(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                currentUser = user;
                currentUser.updateLastLogin();
                dataFacade.updateUser(currentUser);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Register a new user
     * @param userId Unique user ID
     * @param password User's password (min 4 chars)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @return true if registration successful
     */
    public boolean register(String userId, String password, String firstName, 
                           String lastName, String email) {
        // Validation
        if (userId == null || userId.trim().isEmpty()) return false;
        if (userId == null || userId.length() != 5) return false;
        if (password == null || password.length() < 4) return false;
        if (dataFacade.userIdExists(userId)) return false;
        //if (dataFacade.emailExists(email)) return false;
        
        // Create and add new user
        User newUser = new User(userId, password, firstName, lastName, email);
        return dataFacade.addUser(newUser);
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get the currently logged in user
     * @return Current User object or null
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
>>>>>>> main
