package com.model;

import java.time.LocalDateTime;

/**
 * Leaderboard entry for ranking players
 * Automatically updated by GameDataFacade when user progress changes
 */
public class LeaderboardEntry {
    private String userId;
    private String userName;
    private int totalScore;
    private int puzzlesCompleted;
    private LocalDateTime lastUpdated;
    
    /** 
     * Default constructor
     */
    public LeaderboardEntry() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Constructor with all fields
     * @param userId
     * @param userName
     * @param totalScore
     * @param puzzlesCompleted
     */
    public LeaderboardEntry(String userId, String userName, int totalScore, int puzzlesCompleted) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.totalScore = totalScore;
        this.puzzlesCompleted = puzzlesCompleted;
    }
    
    // Getters and Setters
    /**
     * Get user ID
     * @return String userId
     */
    public String getUserId() { 
        return userId; 
    }
    
    /**
     * Set user ID
     * @param userId
     */
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    /**
     * Get user name
     * @return String userName
     */
    public String getUserName() { 
        return userName; 
    }
    
    /**
     * Set user name
     * @param userName
     */
    public void setUserName(String userName) { 
        this.userName = userName; 
    }
    
    /**
     * Get total score
     * @return int totalScore
     */
    public int getTotalScore() { 
        return totalScore; 
    }
    
    /**
     * Set total score
     * @param totalScore
     */
    public void setTotalScore(int totalScore) { 
        this.totalScore = totalScore; 
    }
    
    /**
     * Get number of puzzles completed
     * @return int puzzlesCompleted
     */
    public int getPuzzlesCompleted() { 
        return puzzlesCompleted; 
    }
    
    /**
     * Set number of puzzles completed
     * @param puzzlesCompleted
     */
    public void setPuzzlesCompleted(int puzzlesCompleted) { 
        this.puzzlesCompleted = puzzlesCompleted; 
    }
    
    /**
     * Get last updated timestamp
     * @return LocalDateTime lastUpdated
     */
    public LocalDateTime getLastUpdated() { 
        return lastUpdated; 
    }
    
    /**
     * Set last updated timestamp
     * @param lastUpdated
     */
    public void setLastUpdated(LocalDateTime lastUpdated) { 
        this.lastUpdated = lastUpdated; 
    }
    
    /**
     * String representation
     * @return String representation
     * @Override
     */
    public String toString() {
        return String.format("%s: %d pts (%d puzzles)", userName, totalScore, puzzlesCompleted);
    }
}
