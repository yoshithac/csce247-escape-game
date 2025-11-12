package com.escapegame;

import java.io.IOException;

import com.model.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * Controller for register.fxml
 */
public class RegisterController {

    @FXML
    private TextField txt_userId;

    @FXML
    private TextField txt_password; // use PasswordField in FXML later if desired

    @FXML
    private TextField txt_fullName;  // split into first/last below

    @FXML
    private Label lbl_error;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private void btnRegisterClicked(MouseEvent event) throws IOException {
        String userId = txt_userId.getText();
        String password = txt_password.getText();
        String fullName = txt_fullName.getText();

        lbl_error.setVisible(false);

        if (userId == null || userId.isBlank() ||
            password == null || password.isBlank() ||
            fullName == null || fullName.isBlank()) {
            lbl_error.setText("All fields are required.");
            lbl_error.setVisible(true);
            return;
        }

        // split full name into first/last (best effort)
        String firstName = fullName.trim();
        String lastName = "";
        int spaceIdx = fullName.trim().indexOf(' ');
        if (spaceIdx > 0) {
            firstName = fullName.trim().substring(0, spaceIdx).trim();
            lastName = fullName.trim().substring(spaceIdx + 1).trim();
        }

        // email not present on form — pass empty string
        String email = "";

        boolean ok = authService.register(userId.trim(), password, firstName, lastName, email);

        if (ok) {
            App.setRoot("login");
        } else {
            lbl_error.setText("Registration failed. Ensure ID is 5 chars and password >= 4 chars.");
            lbl_error.setVisible(true);
        }
    }

    @FXML
    private void back(MouseEvent event) throws IOException {
        App.setRoot("home");
    }
}