package com.model;

import java.util.Map;

/**
 * Main interface for all puzzle games
 * Includes save/restore functionality for resume feature
 */
public interface PuzzleGame {
    /**
     * Initialize game with puzzle data from JSON
     * @param puzzleData Map containing puzzle-specific data
     */
    void initialize(Map<String, Object> puzzleData);
    
    /**
     * Process user input and update game state
     * @param input User input string
     * @return true if input was valid, false otherwise
     */
    boolean processInput(String input);
    
    /**
     * Check if game is over (won or lost)
     * @return true if game ended
     */
    boolean isGameOver();
    
    /**
     * Get current game state for display
     * @return Map containing all state data needed for UI
     */
    Map<String, Object> getGameState();
    
    /**
     * Get game type identifier
     * @return Game type (MAZE, MATCHING, CIPHER, etc.)
     */
    String getGameType();
    
    /**
     * Get final game result
     * @return Map with result data (won, time, moves, score)
     */
    Map<String, Object> getResult();
    
    /**
     * Reset game to initial state
     */
    void reset();
    
    /**
     * Save current game state for resume functionality
     * @return Map containing serializable game state
     */
    Map<String, Object> saveState();
    
    /**
     * Restore game from saved state
     * @param savedState Previously saved game state
     */
    void restoreState(Map<String, Object> savedState);
}
