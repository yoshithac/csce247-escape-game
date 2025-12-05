package com.escapegame.controller;

import com.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * WordPuzzleController - UI Controller for Word Puzzle Views (Cipher, Anagram, Riddle)
 * 
 * Extends BasePuzzleController for common functionality:
 * - Timer management
 * - Background setup
 * - Save/back navigation
 * - Game completion handling
 * 
 * This controller handles:
 * - Answer input and submission
 * - Hint display
 * - Previous guesses display
 */
public class WordPuzzleController extends BasePuzzleController {

    // Additional FXML components specific to Word Puzzle
    @FXML private Label categoryLabel;
    @FXML private Label attemptsLabel;
    @FXML private Label promptLabel;
    @FXML private VBox guessesSection;
    @FXML private VBox guessesList;
    @FXML private VBox hintsSection;
    @FXML private VBox hintsList;
    @FXML private Label hintAvailabilityLabel;
    @FXML private TextField answerField;
    @FXML private Button submitButton;
    @FXML private Button hintButton;

    // Game model
    private WordPuzzleGame game;

    // ==================== ABSTRACT METHOD IMPLEMENTATIONS ====================

    @Override
    protected void initializeGame() {
        game = new WordPuzzleGame();
        game.setPuzzleType(puzzle.getPuzzleType());
        game.setPuzzleId(puzzle.getPuzzleId());
        
        // Restore or initialize
        restoreOrInitialize(
            () -> game.restoreState(getSavedGameState()),
            () -> game.initialize(puzzle.getData())
        );
        
        // Load hints AFTER initialize (initialize() resets hints list)
        loadHints();
        
        // Focus on answer field
        if (answerField != null) {
            answerField.requestFocus();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void renderGame() {
        Map<String, Object> state = game.getGameState();
        
        // Get state values
        String prompt = (String) state.get("prompt");
        String category = (String) state.get("category");
        int attemptsUsed = (int) state.get("attemptsUsed");
        int maxAttempts = (int) state.get("maxAttempts");
        List<String> guesses = (List<String>) state.get("guesses");
        List<String> revealedHints = (List<String>) state.get("revealedHints");
        int availableHints = (int) state.get("availableHintsCount");
        
        // Update labels
        if (promptLabel != null) {
            promptLabel.setText(prompt);
        }
        if (categoryLabel != null) {
            categoryLabel.setText("Category: " + (category != null ? category : puzzle.getPuzzleType()));
        }
        if (attemptsLabel != null) {
            attemptsLabel.setText("Attempts: " + attemptsUsed + "/" + maxAttempts);
        }
        
        // Update guesses section
        updateGuessesDisplay(guesses);
        
        // Update hints section
        updateHintsDisplay(revealedHints);
        
        // Update hint availability
        updateHintAvailability(availableHints);
    }

    @Override
    protected Map<String, Object> getGameResult() {
        return game.getResult();
    }

    @Override
    protected Map<String, Object> saveGameState() {
        return game.saveState();
    }

    @Override
    protected String getPuzzleTypeName() {
        String type = puzzle.getPuzzleType();
        if (type == null) return "Word Puzzle";
        
        switch (type.toUpperCase()) {
            case "CIPHER": return "Cipher";
            case "ANAGRAM": return "Anagram";
            case "RIDDLE": return "Riddle";
            default: return "Word Puzzle";
        }
    }

    // ==================== WORD PUZZLE-SPECIFIC METHODS ====================

    /**
     * Load hints from service manager
     */
    private void loadHints() {
        List<Hint> hints = serviceManager.getHintsForPuzzle(puzzle.getPuzzleId());
        game.setHints(hints);
        System.out.println("Loaded " + hints.size() + " hints for puzzle " + puzzle.getPuzzleId());
    }

    /**
     * Update the guesses display
     */
    private void updateGuessesDisplay(List<String> guesses) {
        if (guessesSection == null || guessesList == null) return;
        
        if (guesses != null && !guesses.isEmpty()) {
            guessesSection.setVisible(true);
            guessesSection.setManaged(true);
            guessesList.getChildren().clear();
            
            for (String guess : guesses) {
                Label guessLabel = new Label("✗ " + guess);
                guessLabel.setStyle("-fx-text-fill: #ff6b6b;");
                guessesList.getChildren().add(guessLabel);
            }
        } else {
            guessesSection.setVisible(false);
            guessesSection.setManaged(false);
        }
    }

    /**
     * Update the hints display
     */
    private void updateHintsDisplay(List<String> revealedHints) {
        if (hintsSection == null || hintsList == null) return;
        
        if (revealedHints != null && !revealedHints.isEmpty()) {
            hintsSection.setVisible(true);
            hintsSection.setManaged(true);
            hintsList.getChildren().clear();
            
            for (int i = 0; i < revealedHints.size(); i++) {
                Label hintLabel = new Label("💡 Hint " + (i + 1) + ": " + revealedHints.get(i));
                hintLabel.setStyle("-fx-text-fill: #ffd700;");
                hintLabel.setWrapText(true);
                hintsList.getChildren().add(hintLabel);
            }
        } else {
            hintsSection.setVisible(false);
            hintsSection.setManaged(false);
        }
    }

    /**
     * Update hint availability label and button state
     */
    private void updateHintAvailability(int availableHints) {
        if (hintAvailabilityLabel != null) {
            if (availableHints > 0) {
                hintAvailabilityLabel.setText("💡 " + availableHints + " hint(s) available");
            } else {
                hintAvailabilityLabel.setText("No hints left");
            }
        }
        
        if (hintButton != null) {
            hintButton.setDisable(availableHints <= 0);
        }
    }

    /**
     * Handle answer submission
     */
    @FXML
    private void handleSubmit() {
        if (answerField == null) return;
        
        String answer = answerField.getText().trim();
        if (answer.isEmpty()) {
            return;
        }
        
        game.processInput(answer);
        answerField.clear();
        renderGame();
        
        if (game.isGameOver()) {
            handleWordPuzzleComplete();
        }
    }

    /**
     * Handle hint request
     */
    @FXML
    private void handleHint() {
        game.processInput("HINT");
        renderGame();
        
        if (answerField != null) {
            answerField.requestFocus();
        }
    }

    /**
     * Handle word puzzle completion
     */
    private void handleWordPuzzleComplete() {
        Map<String, Object> result = game.getResult();
        boolean won = (boolean) result.get("won");
        handleGameComplete(won);
    }
}
