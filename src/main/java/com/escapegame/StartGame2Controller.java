package com.escapegame;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for startgame2.fxml
 * The nextButton (">") will navigate to the Puzzle Home screen.
 *
 * If you want the StartGame2 screen to start the model first, uncomment
 * the startModelInBackground() call in onNext() and adjust model code as needed.
 */
public class StartGame2Controller {

    @FXML
    private Button nextButton;

    /**
     * Called from FXML when the ">" button is pressed.
     * Default behavior: navigate to the puzzle home scene.
     */
    @FXML
    private void onNext() {
        // Disable the button to avoid double-clicks while navigating
        nextButton.setDisable(true);

        try {
            // Simple immediate navigation to puzzle home scene:
            App.setRoot("puzzlehome");
        } catch (IOException e) {
            e.printStackTrace();
            // Re-enable on failure so user can try again
            nextButton.setDisable(false);
        }

        // --- Optional: start the model then navigate (uncomment if you want this) ---
        // startModelInBackground();
    }

    /**
     * Optional: start the model on a background thread and navigate once started.
     * This mirrors the approach used elsewhere in your app.
     *
     * If you use this, comment out the direct App.setRoot("puzzlehome") call above
     * (or remove the immediate navigation) so navigation happens after the model starts.
     */
    @SuppressWarnings("unused")
    private void startModelInBackground() {
        nextButton.setDisable(true);
        new Thread(() -> {
            try {
                // Example model startup (adapt to your model API)
                com.model.AuthenticationService authService = new com.model.AuthenticationService();

                // forward current FX user to model auth if available
                com.model.User fxUser = App.getCurrentUser();
                if (fxUser != null) {
                    try {
                        java.lang.reflect.Method m = com.model.AuthenticationService.class
                                .getMethod("setCurrentUser", com.model.User.class);
                        m.invoke(authService, fxUser);
                    } catch (NoSuchMethodException nsme) {
                        try {
                            java.lang.reflect.Field f = com.model.AuthenticationService.class
                                    .getDeclaredField("currentUser");
                            f.setAccessible(true);
                            f.set(authService, fxUser);
                        } catch (Exception ignore) { /* fall back to unauthenticated */ }
                    } catch (Throwable t) {
                        System.err.println("Warning while forwarding current user: " + t);
                    }
                }

                // create and start model (ConsoleView example)
                com.model.ConsoleView view = new com.model.ConsoleView(new java.util.Scanner(System.in));
                com.model.GameController modelController = new com.model.GameController(view, authService);

                // Inject chosen difficulty if available
                String diff = App.getChosenDifficulty();
                if (diff != null) {
                    try {
                        java.lang.reflect.Method setDiff = com.model.GameController.class
                                .getMethod("setSessionDifficulty", String.class);
                        setDiff.invoke(modelController, diff);
                        App.clearChosenDifficulty();
                    } catch (NoSuchMethodException nsme) {
                        // ignore if not available
                    } catch (Throwable t) {
                        System.err.println("Warning injecting difficulty: " + t);
                    }
                }

                // Start model (ensure this does not block the FX thread)
                modelController.start();

                // After model started (or immediately if model runs independently),
                // switch to Puzzle Home on the FX thread:
                Platform.runLater(() -> {
                    try {
                        App.setRoot("puzzlehome");
                    } catch (IOException e) {
                        e.printStackTrace();
                        nextButton.setDisable(false);
                    }
                });

            } catch (Throwable t) {
                t.printStackTrace();
                Platform.runLater(() -> {
                    // Re-enable button on error so user can retry
                    nextButton.setDisable(false);
                });
            }
        }, "Model-Start-Thread").start();
    }
}
