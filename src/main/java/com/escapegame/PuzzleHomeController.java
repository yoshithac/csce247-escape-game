package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * Start screen controller — wired to startgame.fxml
 */
public class PuzzleHomeController implements Initializable {

    @FXML private Button quitButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PuzzleHomeController initialized");
    }

    @FXML
    private void onQuit() {
        System.out.println("Quit button clicked");
        try {
            App.setRoot("mainmenu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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