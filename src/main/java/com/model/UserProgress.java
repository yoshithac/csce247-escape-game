package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User progress entity tracking completed puzzles and game state
 * 
 *   CLEAN SOLUTION: Separate fields for paused puzzles and session data
 * No backward compatibility - requires GameData.json update
 */
public class UserProgress {
    private String userId;
    private List<String> completedPuzzles;
    private Map<String, Integer> puzzleScores;
    private int totalScore;
    
    //   For PAUSED PUZZLE (individual game state)
    private String currentPuzzleId;           // e.g., "maze_h1", "match_m2"
    private Map<String, Object> pausedPuzzleState;  // Player position, matched cards, etc.
    
    //   For SESSION state (difficulty, remaining puzzles, timer)
    private Map<String, Object> sessionState;  // Difficulty, timer, completed types
    
    /**
     * Default constructor
     */
    public UserProgress() {
        this.completedPuzzles = new ArrayList<>();
        this.puzzleScores = new HashMap<>();
        this.totalScore = 0;
    }
    
    /**
     * Constructor with userId
     */
    public UserProgress(String userId) {
        this();
        this.userId = userId;
    }
    
    // ============= GETTERS =============
    
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
    
    public Map<String, Object> getPausedPuzzleState() {
        return pausedPuzzleState;
    }
    
    public Map<String, Object> getSessionState() {
        return sessionState;
    }
    
    /**
     * Get game state - for compatibility with existing service calls
     * Returns paused puzzle state if exists, otherwise session state
     */
    public Map<String, Object> getGameState() {
        if (pausedPuzzleState != null) {
            return pausedPuzzleState;
        }
        return sessionState;
    }
    
    // ============= SETTERS =============
    
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
    
    public void setPausedPuzzleState(Map<String, Object> pausedPuzzleState) {
        this.pausedPuzzleState = pausedPuzzleState;
    }
    
    public void setSessionState(Map<String, Object> sessionState) {
        this.sessionState = sessionState;
    }
    
    /**
     *   For backward compatibility with tests and existing code
     * This setter is kept to avoid breaking existing code
     */
    public void setGameState(Map<String, Object> state) {
        // For compatibility, save to both old and new fields
        // Tests or old code may call this directly
        this.pausedPuzzleState = state;
    }
    
    // ============= BUSINESS METHODS =============
    
    /**
     * Add a completed puzzle with score
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
     */
    public boolean isPuzzleCompleted(String puzzleId) {
        return completedPuzzles.contains(puzzleId);
    }
    
    /**
     * Get count of completed puzzles
     */
    public int getCompletedCount() {
        return completedPuzzles.size();
    }
    
    /**
     *   Save PAUSED PUZZLE state (player position, matched cards, etc.)
     */
    public void savePausedPuzzle(String puzzleId, Map<String, Object> state) {
        this.currentPuzzleId = puzzleId;
        this.pausedPuzzleState = state;
        System.out.println("  Saved paused puzzle: " + puzzleId);
    }
    
    /**
     *   Save SESSION state (difficulty, timer, remaining puzzles)
     */
    public void saveSessionState(Map<String, Object> state) {
        this.sessionState = state;
        System.out.println("  Saved session state");
    }
    
    /**
     *   For compatibility with existing service calls
     * Routes to appropriate method based on puzzleId
     */
    public void saveGameState(String puzzleId, Map<String, Object> state) {
        if ("SESSION".equals(puzzleId)) {
            saveSessionState(state);
        } else {
            savePausedPuzzle(puzzleId, state);
        }
    }
    
    /**
     *   Clear paused puzzle state
     */
    public void clearPausedPuzzle() {
        this.currentPuzzleId = null;
        this.pausedPuzzleState = null;
        System.out.println("  Cleared paused puzzle");
    }
    
    /**
     *   Clear session state
     */
    public void clearSessionState() {
        this.sessionState = null;
        System.out.println("  Cleared session state");
    }
    
    /**
     *   Clear ALL game state (puzzle + session)
     */
    public void clearGameState() {
        clearPausedPuzzle();
        clearSessionState();
    }
    
    /**
     *   Check if user has a paused puzzle
     */
    public boolean hasPausedPuzzle() {
        return currentPuzzleId != null && pausedPuzzleState != null;
    }
    
    /**
     *   Check if user has a saved session
     */
    public boolean hasSavedSession() {
        return sessionState != null && sessionState.containsKey("sessionDifficulty");
    }
    
    /**
     * Check if user has any game in progress
     */
    public boolean hasGameInProgress() {
        return hasPausedPuzzle() || hasSavedSession();
    }
}