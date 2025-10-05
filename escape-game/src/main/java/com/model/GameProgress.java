package com.model;
import java.util.ArrayList;
import java.util.List;

/*
 * This class tracks the progress of the game for each player
 * @author We're Getting an A
 */
public class GameProgress {
    private User currentPlayer;
    private Puzzle currentPuzzle;
    private int totalScore;
    private List<Puzzle> completedPuzzles;
    private List<Puzzle> toDoPuzzles;

    /**
     * Constructor for GameProgress
     * @param puzzles The list of puzzles to be completed
     */
    public GameProgress(List<Puzzle> puzzles) {
        this.toDoPuzzles = puzzles;
        this.completedPuzzles = new ArrayList<>();
        this.totalScore = 0;
    }

    /**
     * Checks the progress of the current player.
     */
    public void checkProgress() {
        
    }

    /**
     * Updates the game progress after completing a puzzle.
     */
    public void updateProgress() {
        
    }
}
