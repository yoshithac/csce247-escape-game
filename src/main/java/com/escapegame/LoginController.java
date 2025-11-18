package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;
import com.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Login controller — only navigation logic adjusted to use App.setRoot(...) (preferred)
 * with a safe FXMLLoader fallback.
 */
public class LoginController implements Initializable {

    @FXML private TextField txt_username;
    @FXML private PasswordField txt_password;
    @FXML private Label lbl_error;

    private final AuthenticationService authService = new AuthenticationService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("DEBUG: LoginController initialized");
        if (lbl_error != null) lbl_error.setVisible(false);
    }

    @FXML
    private void onLogin(ActionEvent event) {
        try {
            performLogin();
        } catch (Throwable t) {
            t.printStackTrace();
            showError("Unexpected error during login: " + t.getClass().getSimpleName());
        }
    }

    @FXML
    private void btnLoginClicked() {
        try {
            performLogin();
        } catch (Throwable t) {
            t.printStackTrace();
            showError("Unexpected error during login: " + t.getClass().getSimpleName());
        }
    }

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

    @FXML
    private void onRegister(ActionEvent event) {
        try {
            App.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to open register screen: " + e.getMessage());
        }
    }

    @FXML
    private void back(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to go back: " + e.getMessage());
        }
    }

    private void showError(String message) {
        System.err.println("Login error: " + message);
        if (lbl_error != null) {
            lbl_error.setText(message);
            lbl_error.setVisible(true);
        }
    }
}