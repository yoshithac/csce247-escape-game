package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * Start screen controller — wired to startgame.fxml
 * Controls navigation from the puzzle home screen,
 * allowing the player to quit to the main menu or
 * proceed to the riddle puzzle.
 */
public class PuzzleHomeController implements Initializable {

    @FXML private Button quitButton;
    /**
     * Called automatically after the FXML is loaded.
     * @param location  the location of the FXML file, or {@code null}
     * @param resources the resource bundle, or {@code null}
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PuzzleHomeController initialized");
    }
     /**
     * Handles the Quit button click. Returns to the main menu screen.
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
     * Handles the Door button click. Navigates to the riddle screen.
     */
    @FXML
    private void onDoor() {
        System.out.println("Door button clicked");
        try {
            App.setRoot("riddle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}