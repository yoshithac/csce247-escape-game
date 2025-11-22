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
    /** Label displaying certificates info or title. */
    @FXML private Label certificatesLabel;

    /**
     * Called when the FXML is loaded.
     * Used to set up or initialize UI components.
     * @param location location of the FXML file
     * @param resources resource bundle for localization
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("CertificatesController initialized");
    }

    /**
     * Handles the "Back" button click.
     * Switches the scene back to the main menu.
     * @param event the action event triggered by the button
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
