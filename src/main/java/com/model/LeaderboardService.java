package com.model;

import java.util.List;

/**
 * LeaderboardService - Manages leaderboard and rankings
 * 
 * Handles:
 * - Top player retrieval
 * - User rank calculation (competition ranking - ties get same rank)
 * - Leaderboard entry lookup
 * 
 * This service is used by GameServiceManager to separate
 * leaderboard-related concerns from the main service.
 */
public class LeaderboardService {
    
    private final GameDataFacade dataFacade;
    
    /**
     * Constructor
     */
    public LeaderboardService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    // ================================================================
    // LEADERBOARD METHODS
    // ================================================================
    
    /**
     * Get top N players from leaderboard
     * @param limit Maximum number of entries to return
     * @return List of top LeaderboardEntry objects (sorted by score)
     */
    public List<LeaderboardEntry> getTopPlayers(int limit) {
        return dataFacade.getLeaderboard(limit);
    }
    
    /**
     * Get user's rank on the leaderboard using competition ranking
     * Players with the same score get the same rank
     * @param userId User ID
     * @return Rank position (1-based) or -1 if not found
     */
    public int getUserRank(String userId) {
        List<LeaderboardEntry> leaderboard = dataFacade.getFullLeaderboard();
        
        // Find the user's score first
        int userScore = -1;
        for (LeaderboardEntry entry : leaderboard) {
            if (entry.getUserId().equals(userId)) {
                userScore = entry.getTotalScore();
                break;
            }
        }
        
        if (userScore == -1) {
            return -1; // User not found
        }
        
        // Count how many players have a higher score (rank = count + 1)
        int rank = 1;
        for (LeaderboardEntry entry : leaderboard) {
            if (entry.getTotalScore() > userScore) {
                rank++;
            }
        }
        
        return rank;
    }
    
    /**
     * Calculate competition rank for a specific entry
     * Players with same score get same rank
     * @param entry Entry to calculate rank for
     * @return Competition rank (1-based)
     */
    public int calculateRank(LeaderboardEntry entry) {
        if (entry == null) {
            return -1;
        }
        
        List<LeaderboardEntry> leaderboard = dataFacade.getFullLeaderboard();
        int entryScore = entry.getTotalScore();
        
        int rank = 1;
        for (LeaderboardEntry e : leaderboard) {
            if (e.getTotalScore() > entryScore) {
                rank++;
            }
        }
        return rank;
    }
    
    /**
     * Get leaderboard entry for specific user
     * @param userId User ID
     * @return LeaderboardEntry or null if not found
     */
    public LeaderboardEntry getUserEntry(String userId) {
        List<LeaderboardEntry> leaderboard = dataFacade.getFullLeaderboard();
        for (LeaderboardEntry entry : leaderboard) {
            if (entry.getUserId().equals(userId)) {
                return entry;
            }
        }
        return null;
    }
    
    /**
     * Get leaderboard entry for specific user (alias for getUserEntry)
     * @param userId User ID
     * @return LeaderboardEntry or null if not found
     */
    public LeaderboardEntry getUserLeaderboardEntry(String userId) {
        return getUserEntry(userId);
    }
    
    /**
     * Get full leaderboard (all users)
     * @return Complete leaderboard sorted by score
     */
    public List<LeaderboardEntry> getFullLeaderboard() {
        return dataFacade.getFullLeaderboard();
    }
}