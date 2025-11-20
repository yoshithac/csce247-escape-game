package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;
import com.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;

/**
 * Opened5 screen controller — wired to startgame.fxml
 */
public class Opened5Controller implements Initializable {

    @FXML private Button quitButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Opened5Controller initialized");
    }

    @FXML
    private void onEscape() {
        System.out.println("Escape button clicked");
        try {
            App.setRoot("endscreen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}