package com.model;
import java.util.ArrayList;
import java.util.List;

/*
 * This class tracks the progress of the game for each player
 * @author We're Getting an A
 */
public class GameProgress extends GameManager{
    private Puzzle currentPuzzle;
    private int totalScore;
    private int completionPercentage;
    private List<Puzzle> completedPuzzles;
    private List<Puzzle> toDoPuzzles;
    private int currentTime;

    /**
     * Constructor for GameProgress
     * @param puzzles The list of puzzles to be completed
     */
    public GameProgress(List<Puzzle> puzzles) {
        totalScore = 0;
        completedPuzzles = new ArrayList<>();
        toDoPuzzles = new ArrayList<>();
        currentTime = 0;
        if (puzzles != null) {
            for (Puzzle p : puzzles) {
                if (p != null && !p.isCompleted()) {
                    this.toDoPuzzles.add(p);
                } else if (p != null && p.isCompleted()) {
                    this.completedPuzzles.add(p);
                }
            }
        }
        this.currentPuzzle = this.toDoPuzzles.isEmpty() ? null : this.toDoPuzzles.get(0);
    }

    /**
     * Checks the progress of the current player.
     */
    public void checkProgress() {
        if (currentPuzzle == null && !toDoPuzzles.isEmpty()) {
            currentPuzzle = toDoPuzzles.get(0);
        }
    }

    /**
     * Updates the game progress score after completing a puzzle.
     */
    public void updateProgress() {
        if (currentPuzzle == null) return;

        currentPuzzle.setCompleted(true);
        if (!completedPuzzles.contains(currentPuzzle)) {
            completedPuzzles.add(currentPuzzle);
        }
        toDoPuzzles.remove(currentPuzzle);

        if (currentPuzzle.getDifficulty() != null) {
            switch (currentPuzzle.getDifficulty()) {
                case EASY: totalScore += 10; break;
                case MEDIUM: totalScore += 20; break;
                case HARD: totalScore += 40; break;
                default: totalScore += 10; break;
            }
        } else {
            totalScore += 10;
        }
        currentPuzzle = toDoPuzzles.isEmpty() ? null : toDoPuzzles.get(0);
        currentTime = getElapsedTime();
        completionPercentage = (int) (((double) completedPuzzles.size() / (completedPuzzles.size() + toDoPuzzles.size())) * 100);
    }

    //helper getters and setters
    /**
     * Gets the total score of the current player
     * @return The total score
     */
    public int getTotalScore() { 
        return totalScore; 
    }

    /**
     * Gets the list of completed puzzles
     * @return The list of completed puzzles
     */
    public List<Puzzle> getCompletedPuzzles() { 
        return completedPuzzles; 
    }

    /**
     * Gets the list of puzzles yet to be completed
     * @return The list of puzzles yet to be completed
     */
    public List<Puzzle> getToDoPuzzles() { 
        return toDoPuzzles; 
    }

    /**
     * Gets the current puzzle
     * @return The current puzzle
     */
    public Puzzle getCurrentPuzzle() { 
        return currentPuzzle; 
    }

    /**
     * Sets the current puzzle
     * @param puzzle The current puzzle
     */
    public void setCompletedPuzzles(List<Puzzle> completedPuzzles) {
        this.completedPuzzles = completedPuzzles;
    }

    /**
     * Sets the list of puzzles to be completed
     * @param toDoPuzzles The list of puzzles yet to be completed
     */
    public void setToDoPuzzles(List<Puzzle> toDoPuzzles) {
        this.toDoPuzzles = toDoPuzzles;
    }

    /**
     * Gets the current time
     * @return The current time
     */
    public int getCurrentTime() {
        return currentTime;
    }
    
    /**
     * Sets the current time
     * @param currentTime The current time
     */
    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }
}
