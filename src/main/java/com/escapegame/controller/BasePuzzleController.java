package com.escapegame.controller;

import com.escapegame.controller.SessionData;
import com.escapegame.util.BackgroundManager;
import com.escapegame.util.DialogHelper;
import com.escapegame.util.SessionTimer;
import com.escapegame.util.ViewNavigator;
import com.model.GameServiceManager;
import com.model.Puzzle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.Map;
import java.util.function.Consumer;

/**
 * BasePuzzleController - Abstract base class for all puzzle controllers
 * 
 * Provides common functionality for puzzle views:
 * - Timer management (setup, display, cleanup)
 * - Background and frame padding
 * - Save/back navigation
 * - Game completion handling
 * - Alert dialogs
 * 
 * Subclasses implement:
 * - initializeGame() - Set up the specific game
 * - renderGame() - Render the game state
 * - getGameResult() - Get the game result for scoring
 * - saveGameState() - Save current game state
 * - getPuzzleTypeName() - Display name for the puzzle type
 * 
 * @author Escape Game Team
 * @version 1.0
 */
public abstract class BasePuzzleController {

    // Common FXML components - subclasses must have these in their FXML
    @FXML protected StackPane rootPane;
    @FXML protected ImageView backgroundImage;
    @FXML protected BorderPane contentPane;
    @FXML protected Label titleLabel;
    @FXML protected Label timerLabel;

    // Services and state
    protected GameServiceManager serviceManager;
    protected Puzzle puzzle;
    private Consumer<Integer> timerListener;

    /**
     * Initialize the controller - called by JavaFX after FXML injection
     * Template method pattern - defines the skeleton of initialization
     */
    @FXML
    public void initialize() {
        try {
            // Get service manager
            serviceManager = GameServiceManager.getInstance();
            
            // Get current puzzle from session
            puzzle = SessionData.getCurrentPuzzle();
            if (puzzle == null) {
                DialogHelper.showError("No puzzle selected!");
                return;
            }
            
            System.out.println(getClass().getSimpleName() + ": Loading puzzle " + puzzle.getPuzzleId());
            
            // Setup background and responsive padding
            setupBackground();
            
            // Initialize the specific game (subclass implements)
            initializeGame();
            
            // Setup shared timer
            setupTimerListener();
            updateTimerDisplay();
            
            // Update title
            if (titleLabel != null) {
                titleLabel.setText(puzzle.getTitle() + " - " + puzzle.getDifficulty());
            }
            
            // Render the game (subclass implements)
            renderGame();
            
            System.out.println(getClass().getSimpleName() + ": Initialization complete");
            
        } catch (Exception e) {
            System.err.println(getClass().getSimpleName() + " initialization error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // ==================== ABSTRACT METHODS (Subclass must implement) ====================
    
    /**
     * Initialize the game model
     * Check SessionData.isResuming() to restore saved game if needed
     */
    protected abstract void initializeGame();
    
    /**
     * Render the current game state to the UI
     */
    protected abstract void renderGame();
    
    /**
     * Get the game result for scoring
     * @return Map containing "won", "time", "moves", etc.
     */
    protected abstract Map<String, Object> getGameResult();
    
    /**
     * Save the current game state
     * @return Map containing game state to persist
     */
    protected abstract Map<String, Object> saveGameState();
    
    /**
     * Get display name for the puzzle type (e.g., "Maze", "Matching Game")
     * @return Display name
     */
    protected abstract String getPuzzleTypeName();
    
    // ==================== BACKGROUND SETUP ====================
    
    /**
     * Setup background image and responsive padding
     */
    protected void setupBackground() {
        if (rootPane != null && backgroundImage != null && contentPane != null) {
            BackgroundManager.setupFrameBackground(rootPane, backgroundImage, contentPane);
        }
    }
    
    // ==================== TIMER MANAGEMENT ====================
    
    /**
     * Setup listener for shared session timer
     * Uses SessionTimer utility (Controller bridges timer to Model)
     */
    private void setupTimerListener() {
        SessionTimer timer = SessionTimer.getInstance();
        
        timerListener = remaining -> updateTimerDisplay();
        timer.addListener(timerListener);
        
        // Initialize timer with values from Model
        int timeLimit = serviceManager.getSessionTimeLimit();
        int elapsed = serviceManager.getSessionElapsedSeconds();
        
        // Start timer (or resume if already running)
        timer.start(timeLimit, elapsed, this::handleTimeUp);
    }
    
    /**
     * Update timer display with current remaining time
     */
    protected void updateTimerDisplay() {
        if (timerLabel != null) {
            int remaining = SessionTimer.getInstance().getRemainingSeconds();
            int minutes = remaining / 60;
            int seconds = remaining % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
            
            // Change color when low on time
            String style;
            if (remaining <= 60) {
                style = "-fx-text-fill: #FF4444; -fx-font-size: 16px; -fx-font-weight: bold;";
            } else if (remaining <= 120) {
                style = "-fx-text-fill: #FFA500; -fx-font-size: 16px; -fx-font-weight: bold;";
            } else {
                style = "-fx-text-fill: #FFD700; -fx-font-size: 16px; -fx-font-weight: bold;";
            }
            timerLabel.setStyle(style);
        }
    }
    
    /**
     * Handle time up - clear puzzle state and end session
     */
    private void handleTimeUp() {
        cleanupTimerListener();
        
        // Stop the timer
        SessionTimer.getInstance().stop();
        
        // Clear paused puzzle state
        serviceManager.clearPausedPuzzle();
        
        // End the session (time ran out)
        serviceManager.endSessionOnTimeout();
        
        // Clear SessionData
        SessionData.clearSession();
        
        // Show timeout message and navigate to main menu
        Platform.runLater(() -> {
            DialogHelper.showTimeUp();
            ViewNavigator.navigateToMainMenu();
        });
    }
    
    /**
     * Remove timer listener to prevent memory leaks
     */
    protected void cleanupTimerListener() {
        if (timerListener != null) {
            SessionTimer.getInstance().removeListener(timerListener);
            timerListener = null;
        }
    }
    
    // ==================== GAME COMPLETION ====================
    
    /**
     * Handle game completion - common logic for all puzzle types
     * @param won Whether the player won
     */
    protected void handleGameComplete(boolean won) {
        if (won) {
            String userId = serviceManager.getCurrentUser().getUserId();
            
            // Calculate score using service
            Map<String, Object> result = getGameResult();
            int score = GameServiceManager.calculateScore(result);
            
            System.out.println("=== " + getClass().getSimpleName() + ": Puzzle Completed ===");
            System.out.println("Puzzle ID: " + puzzle.getPuzzleId());
            System.out.println("Score: " + score);
            
            // Complete puzzle via service manager
            serviceManager.completePuzzle(userId, puzzle.getPuzzleId(), score);
            
            // Mark the door as completed in session
            int doorNum = SessionData.getCurrentDoorNumber();
            if (doorNum > 0) {
                serviceManager.markSessionDoorCompleted(doorNum);
            }
            
            // Clear paused state
            serviceManager.clearPausedPuzzle();
            
            // Verify completion was recorded
            boolean isCompleted = serviceManager.isPuzzleCompleted(userId, puzzle.getPuzzleId());
            System.out.println("Puzzle marked as completed: " + isCompleted);
            
            // Show success message
            DialogHelper.showPuzzleComplete(getPuzzleTypeName(), score, doorNum);
            
        } else {
            // Player lost (max attempts, etc.)
            serviceManager.clearPausedPuzzle();
            System.out.println("Puzzle " + puzzle.getPuzzleId() + " failed");
            DialogHelper.showMaxAttempts();
        }
        
        handleBack();
    }
    
    // ==================== NAVIGATION ====================
    
    /**
     * Handle save and quit
     */
    @FXML
    protected void handleSave() {
        Map<String, Object> gameState = saveGameState();
        serviceManager.savePausedPuzzle(puzzle.getPuzzleId(), gameState);
        
        // Sync elapsed time from timer to model, then save
        syncTimerToModel();
        serviceManager.saveSessionToDatabase();
        
        DialogHelper.showGameSaved();
        handleBack();
    }
    
    /**
     * Handle back navigation (with cleanup)
     */
    @FXML
    protected void handleBack() {
        cleanupTimerListener();
        ViewNavigator.navigateToGameSession();
    }
    
    /**
     * Handle quit without saving
     */
    @FXML
    protected void handleQuitWithoutSaving() {
        // Sync elapsed time from timer to model, then save session time
        syncTimerToModel();
        serviceManager.saveSessionToDatabase();
        handleBack();
    }
    
    /**
     * Sync elapsed time from SessionTimer to Model
     * Call before saving session to database
     */
    private void syncTimerToModel() {
        int elapsed = SessionTimer.getInstance().getElapsedSeconds();
        serviceManager.setSessionElapsedSeconds(elapsed);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Restore saved game state if resuming
     * @param game The game object to restore state to
     * @param initializeAction Action to initialize fresh game if no saved state
     */
    protected void restoreOrInitialize(Runnable restoreAction, Runnable initializeAction) {
        if (SessionData.isResuming()) {
            Map<String, Object> savedState = serviceManager.getPausedPuzzleState();
            if (savedState != null) {
                restoreAction.run();
                SessionData.setResuming(false);
                return;
            }
        }
        initializeAction.run();
    }
    
    /**
     * Check if game is being resumed
     */
    protected boolean isResuming() {
        return SessionData.isResuming();
    }
    
    /**
     * Get saved game state from service manager
     */
    protected Map<String, Object> getSavedGameState() {
        return serviceManager.getPausedPuzzleState();
    }
}