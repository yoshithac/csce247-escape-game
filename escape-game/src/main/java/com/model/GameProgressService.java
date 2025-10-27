package com.model;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing user game progress
 * Handles progress tracking, puzzle completion, and available puzzles
 */
public class GameProgressService {
    private final GameDataFacade dataFacade;
    
    public GameProgressService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    /**
     * Get user's progress data
     * @param userId User ID
     * @return UserProgress object (never null)
     */
    public UserProgress getUserProgress(String userId) {
        return dataFacade.getUserProgress(userId);
    }
    
    /**
     * Mark puzzle as completed for user
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @param score Score achieved
     */
    public void completePuzzle(String userId, String puzzleId, int score) {
        dataFacade.completePuzzle(userId, puzzleId, score);
    }
    
    /**
     * Check if user has completed a specific puzzle
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return true if puzzle is completed
     */
    public boolean isPuzzleCompleted(String userId, String puzzleId) {
        return dataFacade.isPuzzleCompleted(userId, puzzleId);
    }
    
    /**
     * Get list of puzzles available to user (not yet completed)
     * @param userId User ID
     * @return List of available puzzles
     */
    public List<Puzzle> getAvailablePuzzles(String userId) {
        UserProgress progress = dataFacade.getUserProgress(userId);
        return dataFacade.getAllPuzzles().stream()
            .filter(p -> !progress.isPuzzleCompleted(p.getPuzzleId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get completed puzzles for user
     * @param userId User ID
     * @return List of completed puzzles
     */
    public List<Puzzle> getCompletedPuzzles(String userId) {
        UserProgress progress = dataFacade.getUserProgress(userId);
        return dataFacade.getAllPuzzles().stream()
            .filter(p -> progress.isPuzzleCompleted(p.getPuzzleId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get user's completion statistics
     * @param userId User ID
     * @return Map with stats (totalPuzzles, completed, remaining, etc.)
     */
    public Map<String, Integer> getProgressStats(String userId) {
        UserProgress progress = dataFacade.getUserProgress(userId);
        Map<String, Integer> stats = new HashMap<>();
        
        int totalPuzzles = dataFacade.getAllPuzzles().size();
        int completed = progress.getCompletedCount();
        
        stats.put("totalPuzzles", totalPuzzles);
        stats.put("completed", completed);
        stats.put("remaining", totalPuzzles - completed);
        stats.put("totalScore", progress.getTotalScore());
        stats.put("completionPercentage", 
                  totalPuzzles > 0 ? (completed * 100) / totalPuzzles : 0);
        
        return stats;
    }
}

