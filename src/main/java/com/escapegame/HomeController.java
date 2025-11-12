package com.escapegame;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

/**
 * Home screen controller — wired to home.fxml
 */
public class HomeController {

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML
    private void initialize() {
        System.out.println("HomeController initialized");
    }

    @FXML
    private void onLogin() {
        System.out.println("Login button clicked");
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to open login screen:\n" + e.getMessage());
        }
    }

    @FXML
    private void onRegister() {
        System.out.println("Register button clicked");
        try {
            App.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to open register screen:\n" + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Navigation error");
        a.setContentText(message);
        a.showAndWait();
    }
}