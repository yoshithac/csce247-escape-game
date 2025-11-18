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
 * End screen controller — wired to endscreen.fxml
 */
public class EndScreenController implements Initializable {

    @FXML private Button endButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
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