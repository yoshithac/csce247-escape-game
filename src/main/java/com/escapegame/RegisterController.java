package com.escapegame;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.model.AuthenticationService;
import library.App;

public class RegisterController {

    @FXML
    private TextField txt_userId;
    @FXML
    private PasswordField txt_password;
    @FXML
    private TextField txt_fullName;
    @FXML
    private Label lbl_error;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private void btnRegisterClicked(MouseEvent event) throws IOException {
        String userId = txt_userId.getText();
        String password = txt_password.getText();
        String fullName = txt_fullName.getText();

        lbl_error.setVisible(false);

        if (userId == null || userId.isBlank() || password == null || password.isBlank() || fullName == null || fullName.isBlank()) {
            lbl_error.setText("All fields are required.");
            lbl_error.setVisible(true);
            return;
        }

        boolean ok = authService.register(userId.trim(), password.trim(), fullName.trim());

        if (ok) {
            // Registration successful - navigate to login
            App.setRoot("login");
        } else {
            lbl_error.setText("Registration failed. Ensure ID is 5 chars, password >= 4 chars.");
            lbl_error.setVisible(true);
        }
    }

    @FXML
    private void back(MouseEvent event) throws IOException {
        App.setRoot("home");
    }
}