package com.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GameProgressService - Manages user game progress
 * 
 * Handles:
 * - Progress tracking and puzzle completion
 * - Game state save/restore
 * - Paused puzzle management
 * - Session state persistence
 * 
 * This service is used by GameServiceManager to separate
 * progress-related concerns from the main service.
 */
public class GameProgressService {
    
    private final GameDataFacade dataFacade;
    
    /**
     * Constructor
     */
    public GameProgressService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    // ================================================================
    // CORE PROGRESS METHODS
    // ================================================================
    
    /**
     * Get user's progress data
     * @param userId User ID
     * @return UserProgress object or null if not found
     */
    public UserProgress getUserProgress(String userId) {
        return dataFacade.getUserProgress(userId);
    }
    
    /**
     * Save user progress
     * @param progress UserProgress to save
     */
    public void saveUserProgress(UserProgress progress) {
        dataFacade.saveUserProgress(progress);
    }
    
    /**
     * Mark puzzle as completed
     * NOTE: Only clears PAUSED puzzle state, NOT session state
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @param score Score achieved
     */
    public void completePuzzle(String userId, String puzzleId, int score) {
        UserProgress progress = getUserProgress(userId);
        if (progress != null) {
            // Add completed puzzle
            progress.addCompletedPuzzle(puzzleId, score);
            
            // Only clear PAUSED puzzle state, NOT session state
            progress.clearPausedPuzzle();
            
            // Save progress
            saveUserProgress(progress);
            
            System.out.println("✓ Puzzle " + puzzleId + " completed with score " + score);
        }
    }
    
    /**
     * Check if puzzle is completed
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return true if user has completed this puzzle
     */
    public boolean isPuzzleCompleted(String userId, String puzzleId) {
        return dataFacade.isPuzzleCompleted(userId, puzzleId);
    }
    
    /**
     * Get user's total score
     * @param userId User ID
     * @return Total score across all puzzles
     */
    public int getUserTotalScore(String userId) {
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getTotalScore() : 0;
    }
    
    /**
     * Get count of completed puzzles
     * @param userId User ID
     * @return Number of puzzles completed
     */
    public int getCompletedPuzzleCount(String userId) {
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getCompletedCount() : 0;
    }
    
    // ================================================================
    // GAME STATE METHODS
    // ================================================================
    
    /**
     * Save game state (for resuming)
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @param state Game state as Map
     */
    public void saveGameState(String userId, String puzzleId, Map<String, Object> state) {
        UserProgress progress = getUserProgress(userId);
        if (progress != null) {
            progress.saveGameState(puzzleId, state);
            saveUserProgress(progress);
        }
    }
    
    /**
     * Get saved game state
     * @param userId User ID
     * @return Game state Map or null
     */
    public Map<String, Object> getGameState(String userId) {
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getGameState() : null;
    }
    
    /**
     * Check if user has a game in progress
     * @param userId User ID
     * @return true if there's a saved game
     */
    public boolean hasGameInProgress(String userId) {
        UserProgress progress = getUserProgress(userId);
        return progress != null && progress.hasGameInProgress();
    }
    
    /**
     * Get current puzzle ID being played
     * @param userId User ID
     * @return Puzzle ID or null
     */
    public String getCurrentPuzzleId(String userId) {
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getCurrentPuzzleId() : null;
    }
    
    /**
     * Clear game state (after completing)
     * @param userId User ID
     */
    public void clearGameState(String userId) {
        UserProgress progress = getUserProgress(userId);
        if (progress != null) {
            progress.clearGameState();
            saveUserProgress(progress);
        }
    }
    
    // ================================================================
    // PAUSED PUZZLE METHODS
    // ================================================================
    
    /**
     * Save paused puzzle state (for resuming individual puzzles)
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @param state Game state to save
     */
    public void savePausedPuzzle(String userId, String puzzleId, Map<String, Object> state) {
        if (userId == null) return;
        UserProgress progress = getUserProgress(userId);
        if (progress != null) {
            progress.savePausedPuzzle(puzzleId, state);
            saveUserProgress(progress);
        }
    }
    
    /**
     * Get paused puzzle state
     * @param userId User ID
     * @return Paused puzzle state or null
     */
    public Map<String, Object> getPausedPuzzleState(String userId) {
        if (userId == null) return null;
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getPausedPuzzleState() : null;
    }
    
    /**
     * Clear paused puzzle state (after completing puzzle)
     * @param userId User ID
     */
    public void clearPausedPuzzle(String userId) {
        if (userId == null) return;
        UserProgress progress = getUserProgress(userId);
        if (progress != null) {
            progress.clearPausedPuzzle();
            saveUserProgress(progress);
        }
    }
    
    /**
     * Check if user has a paused puzzle
     * @param userId User ID
     * @return true if there's a paused puzzle
     */
    public boolean hasPausedPuzzle(String userId) {
        if (userId == null) return false;
        UserProgress progress = getUserProgress(userId);
        return progress != null && progress.hasPausedPuzzle();
    }
    
    /**
     * Get current paused puzzle ID
     * @param userId User ID
     * @return Puzzle ID or null
     */
    public String getPausedPuzzleId(String userId) {
        if (userId == null) return null;
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getCurrentPuzzleId() : null;
    }
    
    // ================================================================
    // SESSION STATE METHODS
    // ================================================================
    
    /**
     * Check if user has a saved session
     * @param userId User ID
     * @return true if there's a saved session
     */
    public boolean hasSavedSession(String userId) {
        if (userId == null) {
            System.out.println("hasSavedSession: No user ID provided");
            return false;
        }
        UserProgress progress = getUserProgress(userId);
        boolean hasSession = progress != null && progress.hasSavedSession();
        System.out.println("hasSavedSession: " + hasSession + " (progress=" + (progress != null) + 
                          ", sessionState=" + (progress != null ? progress.getSessionState() : null) + ")");
        return hasSession;
    }
    
    /**
     * Get saved session state
     * @param userId User ID
     * @return Session state map or null
     */
    public Map<String, Object> getSavedSessionState(String userId) {
        if (userId == null) return null;
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getSessionState() : null;
    }
    
    // ================================================================
    // STATISTICS METHODS
    // ================================================================
    
    /**
     * Get completed puzzle IDs for user
     * @param userId User ID
     * @return List of completed puzzle IDs
     */
    public List<String> getCompletedPuzzleIds(String userId) {
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getCompletedPuzzles() : List.of();
    }
    
    /**
     * Get completed puzzles as Puzzle objects for user
     * @param userId User ID
     * @return List of completed puzzles
     */
    public List<Puzzle> getCompletedPuzzles(String userId) {
        UserProgress progress = getUserProgress(userId);
        if (progress == null) {
            return List.of();
        }
        return dataFacade.getAllPuzzles().stream()
            .filter(p -> progress.isPuzzleCompleted(p.getPuzzleId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get puzzle scores for user
     * @param userId User ID
     * @return Map of puzzleId -> score
     */
    public Map<String, Integer> getPuzzleScores(String userId) {
        UserProgress progress = getUserProgress(userId);
        return progress != null ? progress.getPuzzleScores() : Map.of();
    }
    
    /**
     * Get best score for a puzzle
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return Best score or 0 if not completed
     */
    public int getBestScore(String userId, String puzzleId) {
        Map<String, Integer> scores = getPuzzleScores(userId);
        return scores.getOrDefault(puzzleId, 0);
    }
    
    /**
     * Get list of puzzles available to user (not yet completed)
     * @param userId User ID
     * @return List of available puzzles
     */
    public List<Puzzle> getAvailablePuzzles(String userId) {
        UserProgress progress = getUserProgress(userId);
        if (progress == null) {
            return dataFacade.getAllPuzzles();
        }
        return dataFacade.getAllPuzzles().stream()
            .filter(p -> !progress.isPuzzleCompleted(p.getPuzzleId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get user's completion statistics
     * @param userId User ID
     * @return Map with stats (totalPuzzles, completed, remaining, etc.)
     */
    public Map<String, Integer> getProgressStats(String userId) {
        UserProgress progress = getUserProgress(userId);
        Map<String, Integer> stats = new HashMap<>();
        
        int totalPuzzles = dataFacade.getAllPuzzles().size();
        int completed = progress != null ? progress.getCompletedCount() : 0;
        
        stats.put("totalPuzzles", totalPuzzles);
        stats.put("completed", completed);
        stats.put("remaining", totalPuzzles - completed);
        stats.put("totalScore", progress != null ? progress.getTotalScore() : 0);
        stats.put("completionPercentage", 
                  totalPuzzles > 0 ? (completed * 100) / totalPuzzles : 0);
        
        return stats;
    }
}