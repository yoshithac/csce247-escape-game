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
     * Returns the list of hints
     * @return List of hints
     */
    public List<Hint> getHints() {
        return hints;
    }


}
