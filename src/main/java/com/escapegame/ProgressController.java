package com.escapegame;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Certificates screen controller — wired to certificates.fxml
 */
public class ProgressController {
    @FXML private Label progressLabel;

    @FXML
    private void initialize() {
        System.out.println("ProgressController initialized");
    }
}