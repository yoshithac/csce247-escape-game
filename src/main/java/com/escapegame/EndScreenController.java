package com.escapegame;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

/**
 * End screen controller — wired to endscreen.fxml
 */
public class EndScreenController {

    @FXML private Button endButton;

    @FXML
    private void initialize() {
        System.out.println("EndScreenController initialized");
    }

    @FXML
    private void onEnd() {
        System.out.println("End button clicked");
        try {
            App.setRoot("mainmenu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}