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
    
    public LeaderboardEntry() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LeaderboardEntry(String userId, String userName, int totalScore, int puzzlesCompleted) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.totalScore = totalScore;
        this.puzzlesCompleted = puzzlesCompleted;
    }
    
    // Getters and Setters
    public String getUserId() { 
        return userId; 
    }
    
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    public String getUserName() { 
        return userName; 
    }
    
    public void setUserName(String userName) { 
        this.userName = userName; 
    }
    
    public int getTotalScore() { 
        return totalScore; 
    }
    
    public void setTotalScore(int totalScore) { 
        this.totalScore = totalScore; 
    }
    
    public int getPuzzlesCompleted() { 
        return puzzlesCompleted; 
    }
    
    public void setPuzzlesCompleted(int puzzlesCompleted) { 
        this.puzzlesCompleted = puzzlesCompleted; 
    }
    
    public LocalDateTime getLastUpdated() { 
        return lastUpdated; 
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) { 
        this.lastUpdated = lastUpdated; 
    }
    
    @Override
    public String toString() {
        return String.format("%s: %d pts (%d puzzles)", userName, totalScore, puzzlesCompleted);
    }
}
