package com.model;
import java.util.ArrayList;
import java.util.List;

/**
 * Container class for all game data stored in gamedata.json
 * This single class holds ALL non-user data:
 * - Puzzles
 * - Hints
 * - User progress (including saved game states)
 * - Certificates
 * - Leaderboard
 */
public class GameData {
    private List<Puzzle> puzzles;
    private List<Hint> hints;
    private List<UserProgress> userProgress;
    private List<Certificate> certificates;
    private List<LeaderboardEntry> leaderboard;
    
    // Default constructor - initializes empty lists
    public GameData() {
        this.puzzles = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.userProgress = new ArrayList<>();
        this.certificates = new ArrayList<>();
        this.leaderboard = new ArrayList<>();
    }
    
    // Constructor with all fields
    public GameData(List<Puzzle> puzzles, List<Hint> hints, 
                    List<UserProgress> userProgress, List<Certificate> certificates,
                    List<LeaderboardEntry> leaderboard) {
        this.puzzles = puzzles != null ? puzzles : new ArrayList<>();
        this.hints = hints != null ? hints : new ArrayList<>();
        this.userProgress = userProgress != null ? userProgress : new ArrayList<>();
        this.certificates = certificates != null ? certificates : new ArrayList<>();
        this.leaderboard = leaderboard != null ? leaderboard : new ArrayList<>();
    }
    
    // Getters and Setters
    public List<Puzzle> getPuzzles() { 
        return puzzles; 
    }
    
    /**
     * Set puzzles list
     * @param puzzles
     */
    public void setPuzzles(List<Puzzle> puzzles) { 
        this.puzzles = puzzles; 
    }
    
    /**
     * Get hints list
     * @return List of hints
     */
    public List<Hint> getHints() { 
        return hints; 
    }
    
    /**
     * Set hints list
     * @param hints
     */
    public void setHints(List<Hint> hints) { 
        this.hints = hints; 
    }
    
    /**
     * Get user progress list
     * @return List of user progress
     */
    public List<UserProgress> getUserProgress() { 
        return userProgress; 
    }
    
    /**
     * Set user progress list
     * @param userProgress
     */
    public void setUserProgress(List<UserProgress> userProgress) { 
        this.userProgress = userProgress; 
    }
    
    /**
     * Get certificates list
     * @return List of certificates
     */
    public List<Certificate> getCertificates() { 
        return certificates; 
    }
    
    /**
     * Set certificates list
     * @param certificates
     */
    public void setCertificates(List<Certificate> certificates) { 
        this.certificates = certificates; 
    }
    
    /**
     * Get leaderboard list
     * @return List of leaderboard entries
     */
    public List<LeaderboardEntry> getLeaderboard() { 
        return leaderboard; 
    }
    
    /**
     * Set leaderboard list
     * @param leaderboard
     */
    public void setLeaderboard(List<LeaderboardEntry> leaderboard) { 
        this.leaderboard = leaderboard; 
    }
}
