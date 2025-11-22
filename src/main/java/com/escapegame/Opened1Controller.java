package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * Opened1 screen controller — wired to startgame.fxml
 * Handles basic navigation from the "opened1" / startgame screen:
 * quitting back to the main menu and moving to the matching screen.
 */
public class Opened1Controller implements Initializable {

    @FXML private Button quitButton;
    /**
     * Called by the FXMLLoader to initialize the controller.
     * @param location  location used to resolve relative paths for the root object, or {@code null}
     * @param resources resources used to localize the root object, or {@code null}
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Opened1Controller initialized");
    }
    /**
    * Handler for the Quit button. Navigates to the main menu.
    */
   @FXML
    private void onQuit() {
        System.out.println("Quit button clicked");
        try {
            App.setRoot("mainmenu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Handler for the Door button. Navigates to the matching screen.
     */
    @FXML
    private void onDoor() {
        System.out.println("Door button clicked");
        try {
            App.setRoot("matching");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}