package com.escapegame.controller;

import com.escapegame.App;
import com.speech.Speak;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * IntroScreen2Controller - Second introduction screen
 * 
 * Shows hallway interior with story text and narration
 * "A whisper echoes through the halls: Find the keys..."
 */
public class IntroScreen2Controller {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Label storyLabel;
    @FXML private Button continueButton;
    
    private static final String STORY_TEXT = 
        "A whisper echoes through the halls: \"Find the keys... before it finds you...\" " +
        "Each room hides a puzzle. Each puzzle guards a key. " +
        "Solve them all to unlock the door... and escape the manor before it's too late.";
    
    private static final String BACKGROUND_PATH = "/images/backgrounds/hallway_interior.png";
    
    @FXML
    public void initialize() {
        System.out.println("=== IntroScreen2Controller.initialize() ===");
        
        // Setup responsive background
        setupBackground();
        
        // Setup story with typewriter effect
        setupStoryDisplay();
        
        // Play narration
        playNarration();
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
    
    private void setupStoryDisplay() {
        // Font is applied via CSS class "story-text"
        
        // Initially hide continue button
        continueButton.setVisible(false);
        continueButton.setOpacity(0);
        
        // Typewriter effect for story text
        typewriterEffect(storyLabel, STORY_TEXT, () -> {
            // Show continue button with fade-in after text completes
            showContinueButton();
        });
    }
    
    private void typewriterEffect(Label label, String text, Runnable onComplete) {
        label.setText("");
        
        Timeline timeline = new Timeline();
        for (int i = 0; i < text.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(50 * (i + 1)),
                e -> label.setText(text.substring(0, index + 1))
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.setOnFinished(e -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
        
        timeline.play();
    }
    
    private void showContinueButton() {
        continueButton.setVisible(true);
        
        FadeTransition fade = new FadeTransition(Duration.millis(500), continueButton);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    
    private void playNarration() {
        // Use text-to-speech in background thread
        new Thread(() -> {
            try {
                Speak.speak(STORY_TEXT);
            } catch (Exception e) {
                System.err.println("TTS error: " + e.getMessage());
            }
        }).start();
    }
    
    @FXML
    private void handleContinue() {
        System.out.println("Starting new game session...");
        try {
            App.setRoot("GameSessionView");
        } catch (Exception e) {
            System.err.println("Error navigating to GameSessionView: " + e.getMessage());
            e.printStackTrace();
        }
    }
}