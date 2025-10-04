package com.model;

/**
 * Represents a puzzle in the escape game
 * @author We're Getting an A
 */
public class Puzzle {
    private String puzzleId;
    private String type;
    private String prompt;
    private String answer;
    private String description;
    private DifficultyLevel difficulty;
    private boolean isCompleted;
    private int maxAttempts;

    /**
     * Plays the puzzle
     */
    public void playPuzzle() {
        
    }

    /**
     * Checks if the puzzle is completed
     * @return true if completed, false otherwise
     */
    public boolean checkComplete() {
        return isCompleted;
    }

    /**
     * Provides a hint for the puzzle
     * @return A hint string
     */
    public String getHint() {
        return "Hint: " + prompt;
    }

    /**
     * Resets the puzzle to its initial state
     */
    public void reset() {
       
    }
}
