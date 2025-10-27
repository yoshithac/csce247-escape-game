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
    
    public UserProgress() {
        this.completedPuzzles = new ArrayList<>();
        this.puzzleScores = new HashMap<>();
        this.totalScore = 0;
    }
    
    public UserProgress(String userId) {
        this();
        this.userId = userId;
    }
    
    // Getters
    public String getUserId() { 
        return userId; 
    }
    
    public List<String> getCompletedPuzzles() { 
        return completedPuzzles; 
    }
    
    public Map<String, Integer> getPuzzleScores() { 
        return puzzleScores; 
    }
    
    public int getTotalScore() { 
        return totalScore; 
    }
    
    public String getCurrentPuzzleId() { 
        return currentPuzzleId; 
    }
    
    public Map<String, Object> getGameState() { 
        return gameState; 
    }
    
    // Setters
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    public void setCompletedPuzzles(List<String> completedPuzzles) { 
        this.completedPuzzles = completedPuzzles; 
    }
    
    public void setPuzzleScores(Map<String, Integer> puzzleScores) { 
        this.puzzleScores = puzzleScores; 
    }
    
    public void setTotalScore(int totalScore) { 
        this.totalScore = totalScore; 
    }
    
    public void setCurrentPuzzleId(String currentPuzzleId) { 
        this.currentPuzzleId = currentPuzzleId; 
    }
    
    public void setGameState(Map<String, Object> gameState) { 
        this.gameState = gameState; 
    }
    
    // Business methods
    public void addCompletedPuzzle(String puzzleId, int score) {
        if (!completedPuzzles.contains(puzzleId)) {
            completedPuzzles.add(puzzleId);
            puzzleScores.put(puzzleId, score);
            totalScore += score;
        }
    }
    
    public boolean isPuzzleCompleted(String puzzleId) {
        return completedPuzzles.contains(puzzleId);
    }
    
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
