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
<<<<<<< HEAD
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
    
=======
    }
    
    /**
     * Constructs a new {@code LeaderboardEntry} with the specified details.
     *
     * @param userId the user's unique identifier
     * @param userName the user's display name
     * @param totalScore the player's total accumulated score
     * @param puzzlesCompleted the number of puzzles completed by the player
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
     * Returns the user ID associated with this leaderboard entry.
     *
     * @return the user's unique identifier
     */
    public String getUserId() { 
        return userId; 
    }

    /**
     * Sets the user ID for this leaderboard entry.
     *
     * @param userId the user's unique identifier
     */
    public void setUserId(String userId) { 
        this.userId = userId; 
    }

    /**
     * Returns the display name of the user.
     *
     * @return the user's display name
     */
    public String getUserName() { 
        return userName; 
    }

    /**
     * Sets the display name of the user.
     *
     * @param userName the user's display name
     */
    public void setUserName(String userName) { 
        this.userName = userName; 
    }

    /**
     * Returns the total score achieved by the user.
     *
     * @return the total score
     */
    public int getTotalScore() { 
        return totalScore; 
    }

    /**
     * Updates the user's total score.
     *
     * @param totalScore the new total score
     */
    public void setTotalScore(int totalScore) { 
        this.totalScore = totalScore; 
    }

    /**
     * Returns the number of puzzles the user has completed.
     *
     * @return the number of puzzles completed
     */
    public int getPuzzlesCompleted() { 
        return puzzlesCompleted; 
    }

    /**
     * Sets the number of puzzles completed by the user.
     *
     * @param puzzlesCompleted the new number of completed puzzles
     */
    public void setPuzzlesCompleted(int puzzlesCompleted) { 
        this.puzzlesCompleted = puzzlesCompleted; 
    }

    /**
     * Returns the timestamp of the last update to this leaderboard entry.
     *
     * @return the last updated timestamp
     */
    public LocalDateTime getLastUpdated() { 
        return lastUpdated; 
    }

    /**
     * Updates the timestamp for when this leaderboard entry was last modified.
     *
     * @param lastUpdated the new last updated timestamp
     */
    public void setLastUpdated(LocalDateTime lastUpdated) { 
        this.lastUpdated = lastUpdated; 
    }

    /**
     * Returns a formatted string representation of this leaderboard entry.
     * The format is: {@code "username: totalScore pts (puzzlesCompleted puzzles)"}.
     * @return a string describing the leaderboard entry
     */
>>>>>>> main
    @Override
    public String toString() {
        return String.format("%s: %d pts (%d puzzles)", userName, totalScore, puzzlesCompleted);
    }
}