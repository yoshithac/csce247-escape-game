package com.escapegame;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for OpenRoom5.fxml — simple version without click audio
 */
public class OpenRoom5Controller implements Initializable {

    @FXML private ImageView bgImage;
    @FXML private Button btnEscape;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load background image from resources
        try {
            URL res = getClass().getResource("/images/Screenshot_2025-11-19_202307.png");
            if (res != null) {
                bgImage.setImage(new Image(res.toExternalForm(), 1440, 900, true, true));
            } else {
                statusLabel.setText("Background image not found.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading background image.");
            System.err.println("Image load error: " + e.getMessage());
        }

        statusLabel.setText("Ready. Click ESCAPE to finish the room.");
    }

    @FXML
    private void onEscapeClicked() {
        // Prevent multiple clicks
        if (btnEscape.isDisabled()) {
            statusLabel.setText("You already escaped!");
            return;
        }

        // Show a dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Escaped!");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations — you escaped the room!");
        alert.showAndWait();

        statusLabel.setText("ESCAPED ✓");
        btnEscape.setDisable(true);
    }

    /** Optional: call this if you need to reset the scene later */
    public void resetRoom() {
        btnEscape.setDisable(false);
        statusLabel.setText("");
        try {
            URL res = getClass().getResource("/images/Screenshot_2025-11-19_202307.png");
            if (res != null) {
                bgImage.setImage(new Image(res.toExternalForm(), 1440, 900, true, true));
            }
        } catch (Exception e) {
            System.err.println("Error reloading background: " + e.getMessage());
        }
    }
}
