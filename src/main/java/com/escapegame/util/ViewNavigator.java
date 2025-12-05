package com.escapegame.util;

import com.escapegame.App;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * ViewNavigator - Centralized navigation utility
 * 
 * Handles all view transitions and navigation-related operations.
 * Provides a single point for view loading, reducing code duplication.
 */
public final class ViewNavigator {
    
    // View name constants
    public static final String GAME_CONTAINER = "GameContainerView";
    public static final String GAME_SESSION = "GameSessionView";
    public static final String MAZE_VIEW = "MazeView";
    public static final String MATCHING_VIEW = "MatchingView";
    public static final String WORD_PUZZLE_VIEW = "WordPuzzleView";
    public static final String PROGRESS_VIEW = "ProgressView";
    public static final String LEADERBOARD_VIEW = "LeaderboardView";
    public static final String CERTIFICATES_VIEW = "CertificatesView";
    public static final String ESCAPE_SCREEN = "EscapeScreenView";
    public static final String INTRO_SCREEN_1 = "IntroScreen1View";
    public static final String INTRO_SCREEN_2 = "IntroScreen2View";
    
    // Private constructor - utility class
    private ViewNavigator() {}
    
    /**
     * Navigate to a view by name
     * @param viewName Name of the view (without .fxml extension)
     * @return true if navigation successful
     */
    public static boolean navigateTo(String viewName) {
        try {
            App.setRoot(viewName);
            return true;
        } catch (Exception e) {
            System.err.println("Navigation error to " + viewName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Navigate to main menu (GameContainerView)
     */
    public static void navigateToMainMenu() {
        navigateTo(GAME_CONTAINER);
    }
    
    /**
     * Navigate to game session (hallway view)
     */
    public static void navigateToGameSession() {
        navigateTo(GAME_SESSION);
    }
    
    /**
     * Navigate to puzzle view based on puzzle type
     * @param puzzleType Type of puzzle (MAZE, MATCHING, CIPHER, etc.)
     * @return true if navigation successful
     */
    public static boolean navigateToPuzzle(String puzzleType) {
        String viewName = getViewNameForPuzzleType(puzzleType);
        return navigateTo(viewName);
    }
    
    /**
     * Get view name for puzzle type
     * @param puzzleType Puzzle type string
     * @return View name
     */
    public static String getViewNameForPuzzleType(String puzzleType) {
        if (puzzleType == null) return WORD_PUZZLE_VIEW;
        
        switch (puzzleType.toUpperCase()) {
            case "MAZE":
                return MAZE_VIEW;
            case "MATCHING":
                return MATCHING_VIEW;
            case "CIPHER":
            case "ANAGRAM":
            case "RIDDLE":
                return WORD_PUZZLE_VIEW;
            default:
                return WORD_PUZZLE_VIEW;
        }
    }
    
    /**
     * Navigate with error handling and fallback
     * @param primaryView Primary view to navigate to
     * @param fallbackView Fallback view if primary fails
     * @param errorMessage Message to show on complete failure
     */
    public static void navigateWithFallback(String primaryView, String fallbackView, String errorMessage) {
        if (!navigateTo(primaryView)) {
            if (!navigateTo(fallbackView)) {
                DialogHelper.showError(errorMessage);
            }
        }
    }
}
