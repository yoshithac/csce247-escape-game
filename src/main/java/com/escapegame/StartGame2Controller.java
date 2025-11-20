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
 * Start 2 screen controller — wired to startgame2.fxml
 */
public class StartGame2Controller implements Initializable {

    @FXML private Button nextButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("StartGame2Controller initialized");
    }

    @FXML
    private void onNext(){
        System.out.println("Next button clicked");
        try {
            App.setRoot("puzzlehome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}