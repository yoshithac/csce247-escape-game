package com.model;

import java.util.Optional;

/**
 * AuthenticationService - Centralized authentication service
 * 
 * Handles all user authentication operations:
 * - Login/logout
 * - Registration
 * - Form validation
 * - User existence checks
 * 
 * This service is used by GameServiceManager and provides
 * a clean separation of authentication concerns.
 */
public class AuthenticationService {
    
    // Validation constants
    public static final int USER_ID_LENGTH = 5;
    public static final int MIN_PASSWORD_LENGTH = 4;
    
    private final GameDataFacade dataFacade;
    private User currentUser;
    
    /**
     * Constructor
     */
    public AuthenticationService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    // ================================================================
    // CORE AUTHENTICATION METHODS
    // ================================================================
    
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
     * Register a new user (basic - without validation)
     * @param userId Unique user ID
     * @param password User's password
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @return true if registration successful
     */
    public boolean register(String userId, String password, String firstName, 
                           String lastName, String email) {
        // Basic null checks
        if (userId == null || userId.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;
        if (dataFacade.userIdExists(userId)) return false;
        if (dataFacade.emailExists(email)) return false;
        
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
    
    // ================================================================
    // VALIDATION METHODS
    // ================================================================
    
    /**
     * Validate login form fields
     * @param userId User ID input
     * @param password Password input
     * @return Error message or null if valid
     */
    public String validateLoginForm(String userId, String password) {
        if (isEmpty(userId)) {
            return "Please enter your User ID";
        }
        if (isEmpty(password)) {
            return "Please enter your password";
        }
        return null; // Valid
    }
    
    /**
     * Validate registration form fields
     * @param userId User ID input
     * @param password Password input
     * @param firstName First name input
     * @param lastName Last name input
     * @param email Email input
     * @return Error message or null if valid
     */
    public String validateRegistrationForm(String userId, String password, 
            String firstName, String lastName, String email) {
        
        // User ID validation
        if (isEmpty(userId)) {
            return "Please enter a User ID";
        }
        if (userId.length() != USER_ID_LENGTH) {
            return "User ID must be exactly " + USER_ID_LENGTH + " characters";
        }
        if (!userId.matches("[a-zA-Z0-9]+")) {
            return "User ID can only contain letters and numbers";
        }
        
        // Password validation
        if (isEmpty(password)) {
            return "Please enter a password";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
        }
        
        // Name validation
        if (isEmpty(firstName)) {
            return "Please enter your first name";
        }
        if (isEmpty(lastName)) {
            return "Please enter your last name";
        }
        
        // Email validation
        if (isEmpty(email)) {
            return "Please enter your email";
        }
        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }
        
        return null; // Valid
    }
    
    // ================================================================
    // CHECK METHODS
    // ================================================================
    
    /**
     * Check if userId exists
     * @param userId User ID to check
     * @return true if userId is already taken
     */
    public boolean userIdExists(String userId) {
        return dataFacade.userIdExists(userId);
    }
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if email is already registered
     */
    public boolean emailExists(String email) {
        return dataFacade.emailExists(email);
    }
    
    // ================================================================
    // COMBINED OPERATIONS (Validation + Action)
    // ================================================================
    
    /**
     * Attempt login with validation
     * @param userId User ID
     * @param password Password
     * @return LoginResult with success status and message
     */
    public LoginResult attemptLogin(String userId, String password) {
        // Validate form first
        String validationError = validateLoginForm(userId, password);
        if (validationError != null) {
            return new LoginResult(false, validationError, null);
        }
        
        // Attempt authentication
        boolean success = login(userId, password);
        
        if (success) {
            return new LoginResult(true, "Login successful!", currentUser);
        } else {
            return new LoginResult(false, "Invalid User ID or password", null);
        }
    }
    
    /**
     * Attempt registration with validation
     * @param userId User ID
     * @param password Password
     * @param firstName First name
     * @param lastName Last name
     * @param email Email
     * @return RegistrationResult with success status and message
     */
    public RegistrationResult attemptRegistration(String userId, String password,
            String firstName, String lastName, String email) {
        
        // Validate form first
        String validationError = validateRegistrationForm(userId, password, firstName, lastName, email);
        if (validationError != null) {
            return new RegistrationResult(false, validationError);
        }
        
        // Check if user ID is taken
        if (userIdExists(userId)) {
            return new RegistrationResult(false, "User ID is already taken");
        }
        
        // Check if email is taken
        if (emailExists(email)) {
            return new RegistrationResult(false, "Email is already registered");
        }
        
        // Attempt registration
        boolean success = register(userId, password, firstName, lastName, email);
        
        if (success) {
            return new RegistrationResult(true, "Registration successful! Please login.");
        } else {
            return new RegistrationResult(false, "Registration failed. Please try again.");
        }
    }
    
    // ================================================================
    // HELPER METHODS
    // ================================================================
    
    /**
     * Check if a string is null or empty
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        // Basic pattern check: contains @ and at least one . after @
        int atIndex = email.indexOf('@');
        if (atIndex < 1) return false;
        int dotIndex = email.lastIndexOf('.');
        return dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }
    
    // ================================================================
    // RESULT CLASSES
    // ================================================================
    
    /**
     * Result of a login attempt
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final User user;

        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }

    /**
     * Result of a registration attempt
     */
    public static class RegistrationResult {
        private final boolean success;
        private final String message;

        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}