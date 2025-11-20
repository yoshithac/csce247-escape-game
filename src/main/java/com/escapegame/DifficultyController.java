package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
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
 * Certificates screen controller — wired to certificates.fxml
 */
public class DifficultyController implements Initializable {
    @FXML private Label difficultyLabel;

    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DifficultyController initialized");
    }

    @FXML
    private void onEasy() {
        System.out.println("Easy button clicked");
        try {
            App.setRoot("startgame");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMedium() {
        System.out.println("Medium button clicked");
        try {
            App.setRoot("startgame");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHard() {
        System.out.println("Hard button clicked");
        try {
            App.setRoot("startgame");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void back(ActionEvent event) {
        try {
            App.setRoot("mainmenu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}