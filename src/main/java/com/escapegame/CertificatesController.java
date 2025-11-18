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
 * Certificates screen controller — wired to certificates.fxml
 */
public class CertificatesController implements Initializable {
    @FXML private Label certificatesLabel;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("CertificatesController initialized");
    }
}