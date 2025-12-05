package com.escapegame.controller;

import com.escapegame.App;
import com.speech.Speak;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * EscapeScreenController - Final escape celebration screen
 * 
 * Shows final escape door with congratulations message
 * Auto-redirects to main menu after delay
 */
public class EscapeScreenController {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Label congratsLabel;
    @FXML private Label escapeLabel;
    @FXML private Label statsLabel;
    @FXML private Label scoreLabel;
    @FXML private Label redirectLabel;
    
    private static final String CONGRATS_TEXT = "Congratulations! You escaped!";
    private static final String BACKGROUND_PATH = "/images/backgrounds/final_escape_door.png";
    private static final int AUTO_REDIRECT_DELAY_SECONDS = 6;
    
    // Session stats passed from GameSessionController
    private static int completionTimeSeconds = 0;
    private static int completionScore = 0;
    
    public static void setCompletionTime(int seconds) {
        completionTimeSeconds = seconds;
    }
    
    public static void setCompletionScore(int score) {
        completionScore = score;
    }
    
    public static int getCompletionScore() {
        return completionScore;
    }
    
    @FXML
    public void initialize() {
        System.out.println("=== EscapeScreenController.initialize() ===");
        
        // Setup responsive background
        setupBackground();
        
        // Setup congratulations display
        setupCongratsDisplay();
        
        // Play narration
        playNarration();
        
        // Schedule auto-redirect to main menu
        scheduleRedirect();
    }
    
    private void setupBackground() {
        // Bind background size to root pane
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
        
        // Load background image
        try {
            java.io.InputStream is = getClass().getResourceAsStream(BACKGROUND_PATH);
            if (is != null) {
                Image img = new Image(is);
                backgroundImage.setImage(img);
                System.out.println("✓ Background loaded: " + BACKGROUND_PATH);
            } else {
                System.err.println("✗ Background not found: " + BACKGROUND_PATH);
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading background: " + e.getMessage());
        }
    }
    
    private void setupCongratsDisplay() {
        // Fonts are applied via CSS classes (congrats-title, escape-subtitle, stats-label, redirect-label)
        
        // Animate congratulations text
        if (congratsLabel != null) {
            congratsLabel.setOpacity(0);
            
            // Fade in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), congratsLabel);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            
            // Scale animation
            ScaleTransition scale = new ScaleTransition(Duration.millis(1500), congratsLabel);
            scale.setFromX(0.5);
            scale.setFromY(0.5);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            // Play both together
            ParallelTransition parallel = new ParallelTransition(fadeIn, scale);
            parallel.play();
        }
        
        // Show stats - time
        if (statsLabel != null) {
            int minutes = completionTimeSeconds / 60;
            int seconds = completionTimeSeconds % 60;
            statsLabel.setText(String.format("⏱ Escape Time: %d minutes %d seconds", minutes, seconds));
        }
        
        // Show stats - score
        if (scoreLabel != null) {
            scoreLabel.setText(String.format("⭐ Total Score: %d points", completionScore));
        }
    }
    
    private void playNarration() {
        // Use text-to-speech in background thread
        new Thread(() -> {
            try {
                Speak.speak(CONGRATS_TEXT);
            } catch (Exception e) {
                System.err.println("TTS error: " + e.getMessage());
            }
        }).start();
    }
    
    private void scheduleRedirect() {
        // Auto-redirect to main menu after delay
        Timeline redirectTimer = new Timeline(
            new KeyFrame(Duration.seconds(AUTO_REDIRECT_DELAY_SECONDS), e -> {
                navigateToMainMenu();
            })
        );
        redirectTimer.play();
    }
    
    private void navigateToMainMenu() {
        System.out.println("Redirecting to main menu...");
        Platform.runLater(() -> {
            try {
                App.setRoot("GameContainerView");
            } catch (Exception e) {
                System.err.println("Error navigating to main menu: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}