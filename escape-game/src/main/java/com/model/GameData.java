package com.model;
import java.util.List;

/**
 * This class holds the game data for the escape game application
 * @author We're Getting an A
 */
public class GameData {
    private List<Puzzle> puzzles;
    private GameProgress gameProgress;
    private Leaderboard leaderboard;
    private List<Hint> hints;
    private List<Certificate> certificates;
    
    /**
     * Constructor for User
     */
    public GameData(List<Puzzle> puzzles, GameProgress gameProgress, Leaderboard leaderboard, List<Hint> hints, List<Certificate> certificates) {
        this.puzzles = puzzles;
        this.gameProgress = gameProgress;
        this.leaderboard = leaderboard;
        this.hints = hints;
        this.certificates = certificates;
    }

    /**
     * Returns the list of puzzles in the game
     * @return List of puzzles
     */
    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

    /**
     * Sets the list of puzzles in the game
     * @param puzzles Puzzle list of puzzles
     */
    public void setPuzzles(List<Puzzle> puzzles) {
        this.puzzles = puzzles;
    }

    /**
     * Returns the current game progress
     * @return GameProgress 
     */
    public GameProgress getGameProgress() {
        return gameProgress;
    }

    /**
     * Sets the game progress
     * @param gameProgress GameProgress
     */
    public void setGameProgress(GameProgress gameProgress) {
        this.gameProgress = gameProgress;
    }

    /**
     * Returns the leaderboard of the game
     * @return Leaderboard object
     */
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    /**
     * Sets the leaderboard for thegame
     * @param leaderboard Leaderboard 
     */
    public void setLeaderboard(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    /*
     * Returns the list of hints
     * @return List of hints
     * @return List of hints applicable to the game
     */
    public List<Hint> getHints() {
        return hints;
    }

    /**
     * Sets the list of hints for the game
     * @param hints List of hints
     */
    public void setHints(List<Hint> hints) {
        this.hints = hints;
    }

    /**
     * Returns the certificates earned in the game
     * @return List of certificates
     */
    public List<Certificate> getCertificates() {
        return certificates;
    }

    /**
     * Sets the list of certificates for the game
     * @param certificates List of certificates
     */
    public void setCertificate(List<Certificate> certificates) {
        this.certificates = certificates;
    }
}
