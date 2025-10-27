package com.model;

/**
 * Controller for authentication flow (login, registration)
 * Handles user authentication UI flow
 */
public class AuthController {
    private final GameView view;
    private final AuthenticationService authService;
    
    public AuthController(GameView view, AuthenticationService authService) {
        this.view = view;
        this.authService = authService;
    }
    
    /**
     * Show authentication menu and handle login/registration
     * @return true if user successfully logged in, false if they chose to exit
     */
    public boolean showAuthMenu() {
        while (true) {
            view.clear();
            view.showMessage("\n" + "=".repeat(50));
            view.showMessage("     WELCOME WHISPERS OF HOLLOW MANOR");
            view.showMessage("=".repeat(50));
            
            String choice = view.showMenu(new String[]{
                "Login",
                "Register New User",
                "Exit"
            });
            
            switch (choice) {
                case "1":
                    if (showLogin()) {
                        return true;  // Login successful
                    }
                    break;
                case "2":
                    showRegistration();
                    break;
                case "3":
                    return false;  // User chose to exit
                default:
                    view.showMessage("Invalid choice. Please try again.");
                    waitForUser();
            }
        }
    }
    
    /**
     * Handle login flow
     * @return true if login successful
     */
    private boolean showLogin() {
        view.clear();
        view.showMessage("\n=== LOGIN ===\n");
        
        String userId = view.getUserInput("User ID: ");
        String password = view.getUserInput("Password: ");
        
        if (authService.login(userId, password)) {
            view.showMessage("\nLogin successful! Welcome, " + 
                           authService.getCurrentUser().getFullName() + "!");
            waitForUser();
            return true;
        } else {
            view.showMessage("\nLogin failed! Invalid credentials.");
            waitForUser();
            return false;
        }
    }
    
    /**
     * Handle registration flow
     */
    private void showRegistration() {
        view.clear();
        view.showMessage("\n=== REGISTER NEW USER ===\n");
        
        String userId = view.getUserInput("Choose User ID (5 chars): ");
        String password = view.getUserInput("Password (min 4 chars): ");
        String firstName = view.getUserInput("First Name: ");
        String lastName = view.getUserInput("Last Name: ");
        String email = view.getUserInput("Email: ");
        
        if (authService.register(userId, password, firstName, lastName, email)) {
            view.showMessage("\nRegistration successful! You can now login.");
        } else {
            view.showMessage("\nRegistration failed! User ID or email may already exist, " +
                           "or password or User id is too short.");
        }
        waitForUser();
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (authService.isLoggedIn()) {
            String userName = authService.getCurrentUser().getFullName();
            authService.logout();
            view.showMessage("\nGoodbye, " + userName + "!");
        }
    }
    
    /**
     * Wait for user to press Enter
     */
    private void waitForUser() {
        view.getUserInput("\nPress Enter to continue...");
    }
}

package com.model;

/**
 * Controller for authentication flow (login, registration)
 * Handles user authentication UI flow
 */
public class AuthController {
    private final GameView view;
    private final AuthenticationService authService;
    
    public AuthController(GameView view, AuthenticationService authService) {
        this.view = view;
        this.authService = authService;
    }
    
    /**
     * Show authentication menu and handle login/registration
     * @return true if user successfully logged in, false if they chose to exit
     */
    public boolean showAuthMenu() {
        while (true) {
            view.clear();
            view.showMessage("\n" + "=".repeat(50));
            view.showMessage("     WELCOME WHISPERS OF HOLLOW MANOR");
            view.showMessage("=".repeat(50));
            
            String choice = view.showMenu(new String[]{
                "Login",
                "Register New User",
                "Exit"
            });
            
            switch (choice) {
                case "1":
                    if (showLogin()) {
                        return true;  // Login successful
                    }
                    break;
                case "2":
                    showRegistration();
                    break;
                case "3":
                    return false;  // User chose to exit
                default:
                    view.showMessage("Invalid choice. Please try again.");
                    waitForUser();
            }
        }
    }
    
    /**
     * Handle login flow
     * @return true if login successful
     */
    private boolean showLogin() {
        view.clear();
        view.showMessage("\n=== LOGIN ===\n");
        
        String userId = view.getUserInput("User ID: ");
        String password = view.getUserInput("Password: ");
        
        if (authService.login(userId, password)) {
            view.showMessage("\nLogin successful! Welcome, " + 
                           authService.getCurrentUser().getFullName() + "!");
            waitForUser();
            return true;
        } else {
            view.showMessage("\nLogin failed! Invalid credentials.");
            waitForUser();
            return false;
        }
    }
    
    /**
     * Handle registration flow
     */
    private void showRegistration() {
        view.clear();
        view.showMessage("\n=== REGISTER NEW USER ===\n");
        
        String userId = view.getUserInput("Choose User ID (5 chars): ");
        String password = view.getUserInput("Password (min 4 chars): ");
        String firstName = view.getUserInput("First Name: ");
        String lastName = view.getUserInput("Last Name: ");
        String email = view.getUserInput("Email: ");
        
        if (authService.register(userId, password, firstName, lastName, email)) {
            view.showMessage("\nRegistration successful! You can now login.");
        } else {
            view.showMessage("\nRegistration failed! User ID or email may already exist, " +
                           "or password or User id is too short.");
        }
        waitForUser();
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (authService.isLoggedIn()) {
            String userName = authService.getCurrentUser().getFullName();
            authService.logout();
            view.showMessage("\nGoodbye, " + userName + "!");
        }
    }
    
    /**
     * Wait for user to press Enter
     */
    private void waitForUser() {
        view.getUserInput("\nPress Enter to continue...");
    }
}
