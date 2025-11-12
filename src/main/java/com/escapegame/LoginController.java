package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;

/**
 * Controller for login.fxml
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txt_username;

    // use PasswordField for secure input; if your FXML uses TextField instead, rename here to TextField
    @FXML
    private PasswordField txt_password;

    @FXML
    private Label lbl_error;

    private final AuthenticationService authService = new AuthenticationService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbl_error.setVisible(false);
        System.out.println("LoginController initialized");
    }

    /**
     * Called by FXML when the login button is pressed (onAction="#onLogin").
     */
    @FXML
    private void onLogin(ActionEvent event) throws IOException {
        performLogin();
    }

    /**
     * If you previously had a MouseEvent handler (btnLoginClicked), you can still call performLogin() there.
     * This adapter ensures both onAction="#onLogin" and other wiring work.
     */
    @FXML
    private void btnLoginClicked() throws IOException {
        performLogin();
    }

    private void performLogin() throws IOException {
        String username = txt_username.getText();
        String password = txt_password.getText();

        lbl_error.setVisible(false);

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            lbl_error.setText("Please enter username and password");
            lbl_error.setVisible(true);
            return;
        }

        boolean ok = authService.login(username.trim(), password);
        if (ok) {
            // navigate to the next scene (adjust as desired)
            App.setRoot("home");
        } else {
            lbl_error.setText("Login failed: invalid credentials");
            lbl_error.setVisible(true);
        }
    }

    /**
     * Navigate to register screen (onAction="#onRegister" inside login.fxml).
     */
    @FXML
    private void onRegister(ActionEvent event) throws IOException {
        App.setRoot("register");
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        App.setRoot("home");
    }
}