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
 * IntroScreen1Controller - First introduction screen
 * 
 * Shows house exterior with story text and narration
 * "Rain pours as you stand before the infamous Hollow Manor..."
 */
public class IntroScreen1Controller {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Label storyLabel;
    @FXML private Button continueButton;
    
    private static final String STORY_TEXT = 
        "Rain pours as you stand before the infamous Hollow Manor, " +
        "the house everyone in town swears is haunted. " +
        "You come to prove the legends wrong, but the moment you step inside, " +
        "the door slams shut behind you.";
    
    private static final String BACKGROUND_PATH = "/images/backgrounds/house_exterior_front.png";
    
    @FXML
    public void initialize() {
        System.out.println("=== IntroScreen1Controller.initialize() ===");
        
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
        System.out.println("Continuing to IntroScreen2...");
        try {
            App.setRoot("IntroScreen2View");
        } catch (Exception e) {
            System.err.println("Error navigating to IntroScreen2: " + e.getMessage());
            // Fallback - go directly to game session
            try {
                App.setRoot("GameSessionView");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}