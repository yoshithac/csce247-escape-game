package com.model;

import java.util.Map;

/**
 * View interface for displaying game UI
 * Separates UI logic from business logic (MVC pattern)
 * Can be implemented for Console, JavaFX, Swing, Web, etc.
 */
public interface GameView {
    /**
     * Display a message to the user
     * @param message Message to display
     */
    void showMessage(String message);
    
    /**
     * Get input from user with a prompt
     * @param prompt Prompt message
     * @return User's input string
     */
    String getUserInput(String prompt);
    
    /**
     * Show a menu and get user's choice
     * @param options Array of menu options
     * @return User's choice (as string)
     */
    String showMenu(String[] options);

    /**
     * Show a menu and get user's choice
     * @param options Array of menu options
     * @return User's choice (as string)
     */
    String showPuzzlesMenu(String[] options);
    
    /**
     * Display current game state
     * @param gameState Map containing game state data
     * @param gameType Type of game (MAZE, MATCHING, etc.)
     */
    void displayGame(Map<String, Object> gameState, String gameType);
    
    /**
     * Show game result (win/lose)
     * @param result Map with result data (won, time, score, etc.)
     */
    void showResult(Map<String, Object> result);
    
    /**
     * Clear the screen/display
     */
    void clear();
}
