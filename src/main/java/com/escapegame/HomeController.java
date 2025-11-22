package com.escapegame;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * Home screen controller — wired to home.fxml
 * This controller manages the application's home screen,
 * handling navigation to the login and register views.
 */
public class HomeController {

    /** Button that navigates to the login screen. */
    @FXML private Button loginButton;
    /** Button that navigates to the registration screen */
    @FXML private Button registerButton;
    
     /**
     * Initializes the home controller
     * Called automatically after FMXL loading.
     */
    @FXML
    private void initialize() {
        System.out.println("HomeController initialized");
    }
    /**
     * Handles the login button click
     * Navigates to the login view.
     */
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
    /**
     * Handles the register button click
     * Navigates to the registration view.
     */
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
    
    /**
     * Displays an error alert dialog if navigation fails
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Navigation error");
        a.setContentText(message);
        a.showAndWait();
    }
}