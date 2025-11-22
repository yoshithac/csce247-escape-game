package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Login controller, handles user input on the login screen and navigation.
 * This controller delegates authentication to {@link AuthenticationService},
 * updates the application's current user on success, and navigates to other
 * screens via {@code App.setRoot(...)}. It contains minimal UI logic and
 * defensive error handling to fall back if {@code App.setRoot} fails.
 */
public class LoginController implements Initializable {

    @FXML private TextField txt_username;
    @FXML private PasswordField txt_password;
    @FXML private Label lbl_error;

    private final AuthenticationService authService = new AuthenticationService();
    
     /**
     * Called after FXML is loaded; hides the error label by default.
     * @param url  not used
     * @param rb   not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("DEBUG: LoginController initialized");
        if (lbl_error != null) lbl_error.setVisible(false);
    }
     /**
     * FXML handler for login action (e.g. form submission).
     * @param event the action event
     */
    @FXML
    private void onLogin(ActionEvent event) {
        try {
            performLogin();
        } catch (Throwable t) {
            t.printStackTrace();
            showError("Unexpected error during login: " + t.getClass().getSimpleName());
        }
    }
    /**
     * Alternate FXML handler for login button click.
     * Reuses {@link #performLogin()}.
     */
    @FXML
    private void btnLoginClicked() {
        try {
            performLogin();
        } catch (Throwable t) {
            t.printStackTrace();
            showError("Unexpected error during login: " + t.getClass().getSimpleName());
        }
    }
      /**
     * Performs validation, calls the authentication service, and navigates on success.
     * Shows user facing errors via {@link #showError(String)} and prints stack traces
     * for unexpected exceptions. On successful authentication the current user is set
     * on {@code App} and the scene is changed to {@code mainmenu}. If {@code App.setRoot}
     * fails, the method falls back to printing the cause and returns.
     */
    private void performLogin() {
        if (lbl_error != null) lbl_error.setVisible(false);

        String username = txt_username == null ? null : txt_username.getText();
        String password = txt_password == null ? null : txt_password.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showError("Please enter username and password");
            return;
        }

        boolean ok;
        try {
            ok = authService.login(username.trim(), password);
        } catch (Throwable t) {
            t.printStackTrace();
            showError("Authentication service error: " + t.getClass().getSimpleName());
            return;
        }

        if (!ok) {
            showError("Login failed: invalid credentials");
            return;
        }

        User currentUser = null;
        try { currentUser = authService.getCurrentUser(); } catch (Throwable ignore) {}

        try {
            try { App.setCurrentUser(currentUser); } catch (Throwable ignore) {}
            App.setRoot("mainmenu");
            return;
        } catch (IOException | NoSuchMethodError | NoClassDefFoundError e) {
            System.err.println("App.setRoot failed — falling back to direct FXMLLoader. Cause:");
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Unexpected error calling App.setRoot:");
            t.printStackTrace();
        }
    }
    /**
     * Navigates to the registration screen.
     * @param event the action event
     */
    @FXML
    private void onRegister(ActionEvent event) {
        try {
            App.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to open register screen: " + e.getMessage());
        }
    }
    /**
     * Navigates back to the home screen.
     * @param event the action event
     */
    @FXML
    private void back(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to go back: " + e.getMessage());
        }
    }
     /**
     * Shows an error message in the UI (and logs to stderr).
     * @param message message to display
     */
    private void showError(String message) {
        System.err.println("Login error: " + message);
        if (lbl_error != null) {
            lbl_error.setText(message);
            lbl_error.setVisible(true);
        }
    }
}