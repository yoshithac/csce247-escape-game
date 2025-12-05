package com.escapegame.controller;

import com.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * ProgressController - Player Progress Display
 * Loads into RIGHT PANEL of GameContainerView
 * No back button needed - user navigates via left panel menu
 */
public class ProgressController {
    @FXML private Label totalScoreLabel;
    @FXML private Label completedLabel;
    @FXML private Label remainingLabel;
    @FXML private Label percentageLabel;
    @FXML private ProgressBar progressBar;
    
    // Difficulty breakdown labels
    @FXML private Label easyCompletedLabel;
    @FXML private Label mediumCompletedLabel;
    @FXML private Label hardCompletedLabel;

    private final GameServiceManager serviceManager = GameServiceManager.getInstance();

    @FXML
    public void initialize() {
        User currentUser = serviceManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        String userId = currentUser.getUserId();
        UserProgress progress = serviceManager.getUserProgress(userId);
        List<Puzzle> allPuzzles = serviceManager.getAllPuzzles();
        
        int totalPuzzles = allPuzzles.size();
        int completed = progress.getCompletedCount();
        int remaining = totalPuzzles - completed;
        int percentage = totalPuzzles > 0 ? (completed * 100) / totalPuzzles : 0;
        
        // Update main stats
        totalScoreLabel.setText(String.valueOf(progress.getTotalScore()));
        completedLabel.setText(String.valueOf(completed));
        remainingLabel.setText(String.valueOf(remaining));
        percentageLabel.setText(percentage + "%");
        progressBar.setProgress(percentage / 100.0);
        
        // Calculate difficulty breakdown
        updateDifficultyBreakdown(allPuzzles, progress);
    }
    
    /**
     * Update difficulty breakdown stats
     */
    private void updateDifficultyBreakdown(List<Puzzle> allPuzzles, UserProgress progress) {
        int easyTotal = 0, easyDone = 0;
        int mediumTotal = 0, mediumDone = 0;
        int hardTotal = 0, hardDone = 0;
        
        for (Puzzle puzzle : allPuzzles) {
            String difficulty = puzzle.getDifficulty();
            boolean isCompleted = progress.isPuzzleCompleted(puzzle.getPuzzleId());
            
            switch (difficulty.toUpperCase()) {
                case "EASY":
                    easyTotal++;
                    if (isCompleted) easyDone++;
                    break;
                case "MEDIUM":
                    mediumTotal++;
                    if (isCompleted) mediumDone++;
                    break;
                case "HARD":
                    hardTotal++;
                    if (isCompleted) hardDone++;
                    break;
            }
        }
        
        // Update labels if they exist
        if (easyCompletedLabel != null) {
            easyCompletedLabel.setText(easyDone + "/" + easyTotal);
        }
        if (mediumCompletedLabel != null) {
            mediumCompletedLabel.setText(mediumDone + "/" + mediumTotal);
        }
        if (hardCompletedLabel != null) {
            hardCompletedLabel.setText(hardDone + "/" + hardTotal);
        }
    }
}