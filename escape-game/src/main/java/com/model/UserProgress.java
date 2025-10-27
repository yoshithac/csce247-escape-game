package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User progress entity tracking completed puzzles and game state
 * Includes save/resume functionality for partially completed games
 */
public class UserProgress {
    private String userId;
    private List<String> completedPuzzles;
    private Map<String, Integer> puzzleScores;
    private int totalScore;
    
    // For save/resume functionality
    private String currentPuzzleId;
    private Map<String, Object> gameState;
    
    /**
     * Default constructor - initializes empty progress
     */
    public UserProgress() {
        this.completedPuzzles = new ArrayList<>();
        this.puzzleScores = new HashMap<>();
        this.totalScore = 0;
    }
    
    /**
     * Constructor with userId
     * @param userId
     */
    public UserProgress(String userId) {
        this();
        this.userId = userId;
    }
    
    // Getters
    /**
     * Get user ID
     * @return String userId
     */
    public String getUserId() { 
        return userId; 
    }
    
    /**
     * Get list of completed puzzle IDs
     * @return List of puzzle IDs
     */
    public List<String> getCompletedPuzzles() { 
        return completedPuzzles; 
    }
    
    /**
     * Get map of puzzle scores
     * @return Map of puzzleId to score
     */
    public Map<String, Integer> getPuzzleScores() { 
        return puzzleScores; 
    }
    
    /**
     * Get total score across all completed puzzles
     * @return int totalScore
     */
    public int getTotalScore() { 
        return totalScore; 
    }
    
    /**
     * Get current puzzle ID for saved game
     * @return String currentPuzzleId
     */
    public String getCurrentPuzzleId() { 
        return currentPuzzleId; 
    }
    
    /**
     * Get saved game state
     * @return Map representing game state
     */
    public Map<String, Object> getGameState() { 
        return gameState; 
    }
    
    // Setters
    /**
     * Set user ID
     * @param userId
     */
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    /**
     * Set list of completed puzzles
     * @param completedPuzzles
     */
    public void setCompletedPuzzles(List<String> completedPuzzles) { 
        this.completedPuzzles = completedPuzzles; 
    }
    
    /**
     * Set puzzle scores map
     * @param puzzleScores
     */
    public void setPuzzleScores(Map<String, Integer> puzzleScores) { 
        this.puzzleScores = puzzleScores; 
    }
    
    /**
     * Set total score
     * @param totalScore
     */
    public void setTotalScore(int totalScore) { 
        this.totalScore = totalScore; 
    }
    
    /**
     * Set current puzzle ID
     * @param currentPuzzleId
     */
    public void setCurrentPuzzleId(String currentPuzzleId) { 
        this.currentPuzzleId = currentPuzzleId; 
    }
    
    /**
     * Set saved game state
     * @param gameState
     */
    public void setGameState(Map<String, Object> gameState) { 
        this.gameState = gameState; 
    }
    
    // Business methods
    /**
     * Add a completed puzzle with score
     * @param puzzleId
     * @param score
     */
    public void addCompletedPuzzle(String puzzleId, int score) {
        if (!completedPuzzles.contains(puzzleId)) {
            completedPuzzles.add(puzzleId);
            puzzleScores.put(puzzleId, score);
            totalScore += score;
        }
    }
    
    /**
     * Check if a puzzle is completed
     * @param puzzleId
     * @return true if completed
     */
    public boolean isPuzzleCompleted(String puzzleId) {
        return completedPuzzles.contains(puzzleId);
    }
    
    /**
     * Get count of completed puzzles
     * @return int count
     */
    public int getCompletedCount() {
        return completedPuzzles.size();
    }
    
    /**
     * Save game state for resume functionality
     * @param puzzleId Current puzzle being played
     * @param state Game state from PuzzleGame.saveState()
     */
    public void saveGameState(String puzzleId, Map<String, Object> state) {
        this.currentPuzzleId = puzzleId;
        this.gameState = state;
    }
    
    /**
     * Clear saved game state (after completion or abandon)
     */
    public void clearGameState() {
        this.currentPuzzleId = null;
        this.gameState = null;
    }
    
    /**
     * Check if user has a game in progress
     * @return true if there's a saved game
     */
    public boolean hasGameInProgress() {
        return currentPuzzleId != null && gameState != null;
    }
}
