package com.model;

import java.util.List;

/**
 * Service for managing leaderboard and rankings
 * Handles top player retrieval and user rank calculation
 */
public class LeaderboardService {
    private final GameDataFacade dataFacade;
    
    /**
     * Constructor for leaderboard service
     */
    public LeaderboardService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    /**
     * Get top players from leaderboard
     * @param limit Maximum number of entries to return
     * @return List of top LeaderboardEntry objects (sorted by score)
     */
    public List<LeaderboardEntry> getTopPlayers(int limit) {
        return dataFacade.getLeaderboard(limit);
    }
    
    /**
     * Get user's rank on the leaderboard
     * @param userId User ID
     * @return Rank position (1-based) or -1 if not found
     */
    public int getUserRank(String userId) {
        return dataFacade.getUserRank(userId);
    }
    
    /**
     * Get leaderboard entry for specific user
     * @param userId User ID
     * @return LeaderboardEntry or null if not found
     */
    public LeaderboardEntry getUserEntry(String userId) {
        List<LeaderboardEntry> leaderboard = dataFacade.getLeaderboard(Integer.MAX_VALUE);
        return leaderboard.stream()
            .filter(e -> e.getUserId().equals(userId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get full leaderboard (all users)
     * @return Complete leaderboard sorted by score
     */
    public List<LeaderboardEntry> getFullLeaderboard() {
        return dataFacade.getLeaderboard(Integer.MAX_VALUE);
    }
}

