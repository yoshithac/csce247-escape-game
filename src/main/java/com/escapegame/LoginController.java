package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import library.App;
import model.*;

public class LoginController implements Initializable {
    @FXML
    private TextField txt_username;

    @FXML
    private TextField txt_password;

    @FXML
    private Label lbl_error;

    // single instance of your backend service (adjust if you already have DI)
    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private void btnLoginClicked(MouseEvent event) throws IOException {
        String username = txt_username.getText();
        String password = txt_password.getText();

        lbl_error.setVisible(false);

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            lbl_error.setText("Please enter username and password");
            lbl_error.setVisible(true);
            return;
        }

        try {
            boolean ok = authService.login(username.trim(), password);
            if (ok) {
                // login succeeded - you said your AuthenticationService sets currentUser and updates last login
                // navigate to home (same as your existing back() usage)
                App.setRoot("home");
            } else {
                lbl_error.setText("Login failed: invalid credentials");
                lbl_error.setVisible(true);
            }
        } catch (Exception ex) {
            // show generic error (avoid exposing stack traces to UI)
            lbl_error.setText("Login failed: " + ex.getMessage());
            lbl_error.setVisible(true);
            ex.printStackTrace();
        }
    }

    @FXML
    private void back(MouseEvent event) throws IOException {
        App.setRoot("home");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize UI state if needed
        lbl_error.setVisible(false);
    }
}
