package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Certificates screen controller — wired to certificates.fxml
 */
public class CertificatesController implements Initializable {
    /** Label displaying certificates info or title */
    @FXML private Label certificatesLabel;

    /**
     * Called automatically when the FXML is loaded.
     * Used to initialize UI components or data.
     * @param location location of the FXML file
     * @param resources resource bundle for localization
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("CertificatesController initialized");
    }

    /**
     * Handles the "Back" button action.
     * Returns the user to the main menu screen.
     * @param event button click event
     */
    @FXML
    private void back(ActionEvent event) {
        try {
            App.setRoot("mainmenu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
