package com.model;
<<<<<<< HEAD
import java.util.*;
=======
import java.util.ArrayList;
import java.util.List;
>>>>>>> main

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
<<<<<<< HEAD
=======
    /**
     * Constructs a {@code GameData} instance with all fields initialized.
     * 
     * @param puzzles the list of the puzzles 
     * @param hints the list of hints
     * @param userProgress the list of user progress records
     * @param certificates the list of certificates
     * @param leaderboard the list of leaderboard entries
     */
    
>>>>>>> main
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
    
    public void setPuzzles(List<Puzzle> puzzles) { 
        this.puzzles = puzzles; 
    }
    
    public List<Hint> getHints() { 
        return hints; 
    }
    
    public void setHints(List<Hint> hints) { 
        this.hints = hints; 
    }
    
    public List<UserProgress> getUserProgress() { 
        return userProgress; 
    }
    
    public void setUserProgress(List<UserProgress> userProgress) { 
        this.userProgress = userProgress; 
    }
    
    public List<Certificate> getCertificates() { 
        return certificates; 
    }
    
    public void setCertificates(List<Certificate> certificates) { 
        this.certificates = certificates; 
    }
    
    public List<LeaderboardEntry> getLeaderboard() { 
        return leaderboard; 
    }
    
    public void setLeaderboard(List<LeaderboardEntry> leaderboard) { 
        this.leaderboard = leaderboard; 
    }
}
