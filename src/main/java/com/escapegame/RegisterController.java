package com.escapegame;

import java.io.IOException;

import com.model.AuthenticationService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * Controller for register.fxml
 * Handles user registration by validating form inputs and
 * calling {@link AuthenticationService#register(String, String, String, String, String)}.
 * Navigates to the login screen on success or shows an error message on failure.
 */
public class RegisterController {

    @FXML
    private TextField txt_userId;

    @FXML
    private TextField txt_password;

    @FXML
    private TextField txt_fullName; 

    @FXML
    private Label lbl_error;

    private final AuthenticationService authService = new AuthenticationService();
    /**
     * Handles the Register button click event.
     * Validates that all required fields are filled, attempts to register
     * the user, and navigates to the login screen if successful. Displays
     * an error message otherwise.
     * @param event the mouse event triggered by the button click
     * @throws IOException if navigation to another scene fails
     */
    @FXML
    private void btnRegisterClicked(MouseEvent event) throws IOException {
        String userId = txt_userId.getText();
        String password = txt_password.getText();
        String email = txt_fullName.getText();

        lbl_error.setVisible(false);

        if (userId == null || userId.isBlank() ||
            password == null || password.isBlank() ||
            email == null || email.isBlank()) {
            lbl_error.setText("All fields are required.");
            lbl_error.setVisible(true);
            return;
        }

        String firstName = "";
        String lastName = "";

        // email not present on form — pass empty string
        String fillEmail = email;

        boolean ok = authService.register(userId.trim(), password, firstName, lastName, email);

        if (ok) {
            App.setRoot("login");
        } else {
            lbl_error.setText("Registration failed. Ensure ID is 5 chars and password >= 4 chars.");
            lbl_error.setVisible(true);
        }
    }
    /**
     * Handles the Back button click event.
     * Navigates back to the home screen.
     * @param event the mouse event triggered by the button click
     * @throws IOException if navigation fails
     */
    @FXML
    private void back(MouseEvent event) throws IOException {
        App.setRoot("home");
    }
}