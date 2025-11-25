package com.escapegame;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for startgame.fxml. The ">" button navigates to startgame2.fxml.
 */
public class StartGameController {

    @FXML
    private Button nextButton;

    /**
     * Called from FXML when the ">" button is pressed.
     * Navigates to the second splash screen (startgame2).
     */
    @FXML
    private void onNext() {
        // Prevent double clicks
        nextButton.setDisable(true);

        try {
            App.setRoot("startgame2"); // loads startgame2.fxml (must exist in your resources)
        } catch (IOException e) {
            e.printStackTrace();
            // Re-enable button so user can try again if navigation failed
            nextButton.setDisable(false);
        }
    }

    // If you later want to start the model before navigating, add a background
    // thread like the examples we used earlier and call App.setRoot("puzzlehome")
    // from Platform.runLater() after the model is ready.
}
