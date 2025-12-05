package com.escapegame.controller;

import com.escapegame.App;
import com.escapegame.util.DialogHelper;
import com.escapegame.util.SessionTimer;
import com.escapegame.util.ViewNavigator;
import com.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.util.Optional;

/**
 * GameSessionController - UI Controller for Game Session View
 * 
 * Sequential Door System:
 * - User must complete doors in order (1→2→3→4→5)
 * - Background image swaps to show opened doors as progress is made
 * - After all 5 doors completed, user can ESCAPE
 * 
 * Timer: Uses centralized timer from GameServiceManager (shared across views)
 */
public class GameSessionController {

    // FXML UI Components
    @FXML private StackPane rootPane;
    @FXML private ImageView hallwayBackground;
    @FXML private Pane doorButtonPane;
    @FXML private Button door1Button;
    @FXML private Button door2Button;
    @FXML private Button door3Button;
    @FXML private Button door4Button;
    @FXML private Button door5Button;
    @FXML private Button backToMenuButton;
    @FXML private Label progressLabel;
    @FXML private Label timerLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label instructionLabel;
    @FXML private HBox keyInventoryBox;  // For displaying collected keys
    
    // Service (business logic)
    private GameServiceManager serviceManager;
    
    // Background images for each completion state
    private static final String[] BACKGROUND_IMAGES = {
        "/images/backgrounds/hallway_interior.png",  // 0 doors completed
        "/images/backgrounds/opened1.png",           // 1 door completed
        "/images/backgrounds/opened2.png",           // 2 doors completed
        "/images/backgrounds/opened3.png",           // 3 doors completed
        "/images/backgrounds/opened4.png",           // 4 doors completed
        "/images/backgrounds/opened5.png"            // 5 doors completed (escape!)
    };
    
    // Door knob positions [x, y, width, height] for each door
    // Positioned to overlay exactly on door knobs in the background image
    // Base image size: 1344x896
    private static final double[][] DOOR_POSITIONS = {
        {280, 500, 40, 60},    // Door 1 - Red door (far left) - knob
        {495, 510, 35, 50},    // Door 2 - Blue door (left of center) - knob
        {705, 500, 30, 40},    // Door 3 - Back door (center) - knob  
        {854, 514, 30, 40},    // Door 4 - Right of center - knob
        {1152, 550, 40, 60}    // Door 5 - Green door (far right) - knob
    };
    
    // Back to Menu button position [x, y, width, height]
    // Positioned to overlay QUIT text at bottom center of opened*.png images
    private static final double[] BACK_BUTTON_POSITION = {672, 770, 180, 60};  // Center bottom
    
    private static final double BASE_WIDTH = 1344.0;
    private static final double BASE_HEIGHT = 896.0;

    @FXML
    public void initialize() {
        serviceManager = GameServiceManager.getInstance();
        
        System.out.println("=== GameSessionController.initialize() ===");
        
        // Setup responsive layout
        setupResponsiveLayout();
        
        // Initialize or restore session
        initializeSession();
        
        // Position door buttons and update visuals after scene is ready
        Platform.runLater(() -> {
            positionDoorButtons();
            positionBackToMenuButton();
            updateBackgroundForProgress();
            updateDoorStates();
        });
        
        // Start UI timer
        startUITimer();
        
        // Update view
        updateView();
    }
    
    /**
     * Initialize session - delegates to service
     */
    private void initializeSession() {
        System.out.println("=== initializeSession() ===");
        System.out.println("hasActiveSession: " + serviceManager.hasActiveSession());
        
        // Check if we already have an active session in memory (returning from puzzle)
        if (serviceManager.hasActiveSession()) {
            System.out.println("Returning to active session - refreshing completion status");
            serviceManager.refreshSessionCompletionStatus();
            System.out.println("After refresh - completed count: " + serviceManager.getSessionCompletedCount());
            // Timer continues running - don't reset
        } else {
            // No active session in memory - need to start or restore
            // Stop any stale timer first
            SessionTimer.getInstance().stop();
            
            // Try to restore from database
            boolean restored = serviceManager.restoreSession();
            System.out.println("Tried to restore session: " + restored);
            
            if (!restored) {
                // Start new session with selected difficulty
                String difficulty = SessionData.getDifficulty();
                if (difficulty == null) difficulty = "EASY";
                
                System.out.println("Starting new session with difficulty: " + difficulty);
                serviceManager.startNewSession(difficulty);
            } else {
                System.out.println("Restored session from database - completed: " + serviceManager.getSessionCompletedCount());
            }
        }
        
        // Update difficulty label
        if (difficultyLabel != null) {
            difficultyLabel.setText("Difficulty: " + serviceManager.getSessionDifficulty());
        }
    }
    
    // ==================== VIEW SETUP ====================
    
    private void setupResponsiveLayout() {
        // Bind background size
        hallwayBackground.fitWidthProperty().bind(rootPane.widthProperty());
        hallwayBackground.fitHeightProperty().bind(rootPane.heightProperty());
        doorButtonPane.prefWidthProperty().bind(rootPane.widthProperty());
        doorButtonPane.prefHeightProperty().bind(rootPane.heightProperty());
        
        // Re-position buttons on resize
        rootPane.widthProperty().addListener((obs, old, val) -> {
            positionDoorButtons();
            positionBackToMenuButton();
        });
        rootPane.heightProperty().addListener((obs, old, val) -> {
            positionDoorButtons();
            positionBackToMenuButton();
        });
        
        // Load initial background
        loadBackgroundImage(0);
    }
    
    /**
     * Update background image based on how many doors are completed
     */
    private void updateBackgroundForProgress() {
        try {
            int completed = serviceManager.getSessionCompletedCount();
            System.out.println("Updating background for " + completed + " completed doors");
            loadBackgroundImage(completed);
        } catch (Exception e) {
            System.err.println("Error updating background: " + e.getMessage());
            loadBackgroundImage(0);  // Fallback to first image
        }
    }
    
    /**
     * Load background image for given completion count
     */
    private void loadBackgroundImage(int completedCount) {
        // Clamp to valid index
        int imageIndex = Math.min(completedCount, BACKGROUND_IMAGES.length - 1);
        String imagePath = BACKGROUND_IMAGES[imageIndex];
        
        System.out.println("Loading background: " + imagePath);
        
        try {
            java.io.InputStream is = getClass().getResourceAsStream(imagePath);
            if (is != null) {
                Image img = new Image(is);
                if (!img.isError()) {
                    hallwayBackground.setImage(img);
                    System.out.println("✓ Background loaded: " + imagePath);
                } else {
                    System.err.println("✗ Image error for: " + imagePath);
                    loadFallbackBackground();
                }
            } else {
                System.err.println("✗ Resource not found: " + imagePath);
                loadFallbackBackground();
            }
        } catch (Exception e) {
            System.err.println("✗ Exception loading: " + imagePath + " - " + e.getMessage());
            loadFallbackBackground();
        }
    }
    
    private void loadFallbackBackground() {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(BACKGROUND_IMAGES[0]);
            if (is != null) {
                hallwayBackground.setImage(new Image(is));
            }
        } catch (Exception e) {
            System.err.println("Could not load fallback background");
        }
    }
    
    private void positionDoorButtons() {
        double width = rootPane.getWidth();
        double height = rootPane.getHeight();
        if (width <= 0 || height <= 0) return;
        
        double scaleX = width / BASE_WIDTH;
        double scaleY = height / BASE_HEIGHT;
        
        Button[] buttons = {door1Button, door2Button, door3Button, door4Button, door5Button};
        
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == null) continue;
            
            double x = DOOR_POSITIONS[i][0] * scaleX;
            double y = DOOR_POSITIONS[i][1] * scaleY;
            double w = DOOR_POSITIONS[i][2] * scaleX;
            double h = DOOR_POSITIONS[i][3] * scaleY;
            
            buttons[i].setLayoutX(x - w/2);
            buttons[i].setLayoutY(y - h/2);
            buttons[i].setPrefWidth(w);
            buttons[i].setPrefHeight(h);
        }
    }
    
    /**
     * Position the Back to Menu button to overlay QUIT text
     */
    private void positionBackToMenuButton() {
        if (backToMenuButton == null) return;
        
        double width = rootPane.getWidth();
        double height = rootPane.getHeight();
        if (width <= 0 || height <= 0) return;
        
        double scaleX = width / BASE_WIDTH;
        double scaleY = height / BASE_HEIGHT;
        
        double x = BACK_BUTTON_POSITION[0] * scaleX;
        double y = BACK_BUTTON_POSITION[1] * scaleY;
        double w = BACK_BUTTON_POSITION[2] * scaleX;
        double h = BACK_BUTTON_POSITION[3] * scaleY;
        
        // Ensure minimum size for readability
        w = Math.max(w, 120);
        h = Math.max(h, 35);
        
        backToMenuButton.setLayoutX(x - w/2);
        backToMenuButton.setLayoutY(y - h/2);
        backToMenuButton.setPrefWidth(w);
        backToMenuButton.setPrefHeight(h);
    }
    
    /**
     * Update door button states based on completion
     */
    private void updateDoorStates() {
        try {
            int completedCount = serviceManager.getSessionCompletedCount();
            Button[] buttons = {door1Button, door2Button, door3Button, door4Button, door5Button};
            
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i] == null) continue;
                
                int doorNum = i + 1;
                buttons[i].getStyleClass().removeAll("door-completed", "door-available", "door-locked");
                
                if (doorNum <= completedCount) {
                    // Completed
                    buttons[i].getStyleClass().add("door-completed");
                    buttons[i].setDisable(true);
                } else if (doorNum == completedCount + 1) {
                    // Next available door
                    buttons[i].getStyleClass().add("door-available");
                    buttons[i].setDisable(false);
                } else {
                    // Locked (future doors)
                    buttons[i].getStyleClass().add("door-locked");
                    buttons[i].setDisable(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating door states: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== DOOR CLICK HANDLERS ====================
    
    @FXML private void handleDoor1Click() { handleDoorClick(1); }
    @FXML private void handleDoor2Click() { handleDoorClick(2); }
    @FXML private void handleDoor3Click() { handleDoorClick(3); }
    @FXML private void handleDoor4Click() { handleDoorClick(4); }
    @FXML private void handleDoor5Click() { handleDoorClick(5); }
    
    private void handleDoorClick(int doorNum) {
        int completedCount = serviceManager.getSessionCompletedCount();
        int nextDoor = completedCount + 1;
        
        // Only allow clicking the next sequential door
        if (doorNum != nextDoor) {
            if (doorNum < nextDoor) {
                showAlert("This puzzle is already completed!");
            } else {
                showAlert("Complete door " + nextDoor + " first!");
            }
            return;
        }
        
        // Get puzzle from service
        Optional<Puzzle> puzzleOpt = serviceManager.getSessionPuzzleForDoor(doorNum);
        if (!puzzleOpt.isPresent()) {
            showError("No puzzle assigned to this door!");
            return;
        }
        
        Puzzle puzzle = puzzleOpt.get();
        System.out.println("Opening door " + doorNum + " with puzzle: " + puzzle.getPuzzleId());
        
        // Store in SessionData for puzzle controller
        SessionData.setCurrentPuzzle(puzzle);
        SessionData.setCurrentDoorNumber(doorNum);
        
        // ✅ FIX: Check if there's a saved state for this specific puzzle
        // If the user saved and quit this puzzle, we should resume from saved state
        boolean shouldResume = false;
        if (serviceManager.hasPausedPuzzle()) {
            String pausedPuzzleId = serviceManager.getPausedPuzzleId();
            if (pausedPuzzleId != null && pausedPuzzleId.equals(puzzle.getPuzzleId())) {
                shouldResume = true;
                System.out.println("✓ Found saved state for puzzle " + pausedPuzzleId + " - will resume");
            }
        }
        SessionData.setResuming(shouldResume);
        
        // Pause timer and save session
        stopUITimer();
        serviceManager.saveSessionToDatabase();
        
        // Navigate to puzzle view
        try {
            String viewName = GameServiceManager.getViewNameForPuzzleType(puzzle.getPuzzleType());
            App.setRoot(viewName);
        } catch (Exception e) {
            showError("Error loading puzzle: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== TIMER ====================
    
    private void startUITimer() {
        SessionTimer timer = SessionTimer.getInstance();
        
        // Register as timer listener
        timer.addListener(this::onTimerTick);
        
        // Initialize timer with values from Model and start
        int timeLimit = serviceManager.getSessionTimeLimit();
        int elapsed = serviceManager.getSessionElapsedSeconds();
        timer.start(timeLimit, elapsed, this::handleTimeUp);
        
        // Update display immediately
        updateTimerDisplay();
    }
    
    private void stopUITimer() {
        // Remove listener but don't stop the timer (it continues in puzzle views)
        SessionTimer.getInstance().removeListener(this::onTimerTick);
    }
    
    /**
     * Called on each timer tick
     */
    private void onTimerTick(Integer remainingSeconds) {
        updateTimerDisplay();
    }
    
    private void updateTimerDisplay() {
        int remaining = SessionTimer.getInstance().getRemainingSeconds();
        int minutes = remaining / 60;
        int seconds = remaining % 60;
        
        if (timerLabel != null) {
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
            
            // Change color when low on time
            if (remaining <= 60) {
                timerLabel.setStyle("-fx-text-fill: #FF4444; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else if (remaining <= 120) {
                timerLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                timerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            }
        }
    }
    
    private void handleTimeUp() {
        stopUITimer();
        
        // Stop the timer
        SessionTimer.getInstance().stop();
        
        // Use Platform.runLater to show alert after animation frame completes
        Platform.runLater(() -> {
            showAlert("Time's up! Session ended.");
            serviceManager.clearSession();
            SessionData.clearSession();
            navigateToMenu();
        });
    }
    
    // ==================== VIEW UPDATES ====================
    
    private void updateView() {
        // Update progress
        int completed = serviceManager.getSessionCompletedCount();
        if (progressLabel != null) {
            progressLabel.setText("Progress: " + completed + "/5");
        }
        
        // Update key inventory display
        updateKeyInventory();
        
        // Update instruction
        if (instructionLabel != null) {
            if (completed == 0) {
                instructionLabel.setText("Click door 1 to start your escape!");
            } else if (completed < 5) {
                instructionLabel.setText("Great progress! Complete door " + (completed + 1) + " next");
            } else {
                instructionLabel.setText("All doors completed! You can now ESCAPE!");
            }
        }
        
        // Update timer display
        updateTimerDisplay();
        
        // Check victory
        if (serviceManager.isSessionComplete()) {
            Platform.runLater(this::handleVictory);
        }
    }
    
    /**
     * Update the key inventory display
     * Shows key emoji for each completed door with tooltip
     */
    private void updateKeyInventory() {
        if (keyInventoryBox == null) return;
        
        // Clear existing keys (except "Keys:" label)
        keyInventoryBox.getChildren().removeIf(node -> node instanceof Label && 
            ((Label) node).getText().startsWith("🗝") || 
            ((Label) node).getText().equals("No keys yet"));
        
        int completedCount = serviceManager.getSessionCompletedCount();
        
        if (completedCount == 0) {
            Label noKeysLabel = new Label("No keys yet");
            noKeysLabel.getStyleClass().add("no-keys-label");
            keyInventoryBox.getChildren().add(noKeysLabel);
            return;
        }
        
        // Add key emoji for each completed door
        for (int door = 1; door <= 5; door++) {
            if (serviceManager.isSessionDoorCompleted(door)) {
                Label keyLabel = new Label("🗝");
                keyLabel.getStyleClass().add("key-icon");
                keyLabel.setPickOnBounds(true);
                
                Tooltip tooltip = new Tooltip("Key for Room " + door);
                tooltip.setShowDelay(javafx.util.Duration.millis(100));
                Tooltip.install(keyLabel, tooltip);
                
                keyInventoryBox.getChildren().add(keyLabel);
            }
        }
    }
    
    private void handleVictory() {
        stopUITimer();
        
        // Sync elapsed time from timer to model FIRST
        SessionTimer timer = SessionTimer.getInstance();
        int elapsed = timer.getElapsedSeconds();
        serviceManager.setSessionElapsedSeconds(elapsed);
        
        // Stop the timer completely
        timer.stop();
        
        String difficulty = serviceManager.getSessionDifficulty();
        String userId = serviceManager.getCurrentUser().getUserId();
        
        // Calculate total session score from all completed puzzles
        int totalScore = calculateSessionScore();
        
        // Award session certificate
        serviceManager.awardSessionCertificate(userId, difficulty, totalScore, elapsed);
        System.out.println("🏆 Session certificate awarded: " + difficulty + " - " + totalScore + " pts - " + 
                          CertificateService.formatTime(elapsed));
        
        // Pass completion data to escape screen
        EscapeScreenController.setCompletionTime(elapsed);
        EscapeScreenController.setCompletionScore(totalScore);
        
        // Clear session data
        serviceManager.clearSession();
        SessionData.clearSession();
        
        // Navigate to escape screen
        try {
            App.setRoot("EscapeScreenView");
        } catch (Exception e) {
            System.err.println("Error navigating to escape screen: " + e.getMessage());
            // Fallback to main menu
            navigateToMenu();
        }
    }
    
    /**
     * Calculate total score from all completed puzzles in the session
     */
    private int calculateSessionScore() {
        int totalScore = 0;
        String userId = serviceManager.getCurrentUser().getUserId();
        
        for (int door = 1; door <= 5; door++) {
            if (serviceManager.isSessionDoorCompleted(door)) {
                Optional<Puzzle> puzzleOpt = serviceManager.getSessionPuzzleForDoor(door);
                if (puzzleOpt.isPresent()) {
                    String puzzleId = puzzleOpt.get().getPuzzleId();
                    int score = serviceManager.getBestScore(userId, puzzleId);
                    totalScore += score;
                }
            }
        }
        
        return totalScore;
    }
    
    @FXML
    private void handleBackToMenu() {
        stopUITimer();
        
        // Pause timer and sync elapsed time to model
        SessionTimer timer = SessionTimer.getInstance();
        timer.pause();
        serviceManager.setSessionElapsedSeconds(timer.getElapsedSeconds());
        serviceManager.saveSessionToDatabase();
        
        navigateToMenu();
    }
    
    private void navigateToMenu() {
        try {
            // Stop timer completely when leaving to main menu
            SessionTimer.getInstance().stop();
            App.setRoot("GameContainerView");
        } catch (Exception e) {
            showError("Error navigating to menu: " + e.getMessage());
        }
    }
    
    // ==================== DIALOGS ====================
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Session");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}