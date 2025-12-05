package com.escapegame.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * DialogHelper - Centralized dialog utility
 * 
 * Provides standardized alert dialogs throughout the application.
 * Ensures consistent styling and behavior for all dialogs.
 */
public final class DialogHelper {
    
    // Private constructor - utility class
    private DialogHelper() {}
    
    /**
     * Show an information alert
     * @param message Message to display
     */
    public static void showInfo(String message) {
        showInfo("Information", message);
    }
    
    /**
     * Show an information alert with custom title
     * @param title Dialog title
     * @param message Message to display
     */
    public static void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Show an error alert
     * @param message Error message to display
     */
    public static void showError(String message) {
        showError("Error", message);
    }
    
    /**
     * Show an error alert with custom title
     * @param title Dialog title
     * @param message Error message to display
     */
    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Show a warning alert
     * @param message Warning message to display
     */
    public static void showWarning(String message) {
        showWarning("Warning", message);
    }
    
    /**
     * Show a warning alert with custom title
     * @param title Dialog title
     * @param message Warning message to display
     */
    public static void showWarning(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Show a success message for puzzle completion
     * @param score Score achieved
     * @param doorNumber Door number (for key message), or -1 for no door
     */
    public static void showPuzzleComplete(String puzzleType, int score, int doorNumber) {
        String keyMessage;
        if (doorNumber > 0) {
            keyMessage = String.format("%s Completed!\n\nScore: %d points\n\n🗝 You found Key for Room %d!", 
                puzzleType, score, doorNumber);
        } else {
            keyMessage = String.format("%s Completed!\n\nScore: %d points", puzzleType, score);
        }
        showInfo("Puzzle Complete", keyMessage);
    }
    
    /**
     * Show game saved confirmation
     */
    public static void showGameSaved() {
        showInfo("Game Saved", "Game saved! You can resume from this point later.");
    }
    
    /**
     * Show time up message
     */
    public static void showTimeUp() {
        showInfo("Time's Up!", "⏰ Time's Up!\n\nThe session has ended.\nYou ran out of time to escape the manor.");
    }
    
    /**
     * Show max attempts reached message
     */
    public static void showMaxAttempts() {
        showInfo("Game Over", "❌ Max Attempts Reached!\n\nYou've used all your attempts.\nYou can try this puzzle again.");
    }
    
    /**
     * Show a confirmation dialog
     * @param title Dialog title
     * @param message Confirmation message
     * @return true if user clicked OK
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
