package com.model;

/**
 * Represents a hint that can be given to players in the escape game.
 * Manages how many hints a player can use based on the difficulty level
 * 
 * @author We're Getting an A
 */
public class Hint extends GameManager {
    private int hintsUsed;
    private int maxHints;
    private String puzzleId;
    private String hintText;

    /**
     * Constructor for Hint
     * @param puzzleId the ID of the puzzle this hint belongs to
     * @param hintText the text of the hint to display
     * @param maxHints the maximum number of hints allowed
     */
    public Hint(String puzzleId, String hintText, int maxHints) {
        this.puzzleId = puzzleId;
        this.hintText = hintText;
        this.maxHints = maxHints;
        this.hintsUsed = 0;
    }

    /**
     * Provides a hint for the specified puzzle if available.
     * Increments the number of hints used each time a hint is provided.
     * @param puzzleId the ID of the puzzle for which a hint is requested
     * @return the hint text if available, or a message indicating no more hints can be used
     */
    public String provideHint(String puzzleId) {
        if (canUseHint()) {
            hintsUsed++;
            return hintText;
        }
        return "No more hints available.";
    }

    /**
     * Checks whether player can still use a hint.
     * @return true if hints remaining are available, false otherwise
     */
    public boolean canUseHint() {
        return hintsUsed < maxHints;
    }

    /**
     * Resets the number of hints used back to zero.
     */
    public void resetHints() {
        hintsUsed = 0;
    }

    /**
     * Sets the maximum number of hints based on the game's difficulty level.
     * @param maxHints the default maximum number of hints (ignored, as difficulty determines actual value)
     */
    public void setMaxHints(int maxHints) {
        if (getDifficulty() == DifficultyLevel.EASY) {
            this.maxHints = 5;
        } else if (getDifficulty() == DifficultyLevel.MEDIUM) {
            this.maxHints = 3;
        } else if (getDifficulty() == DifficultyLevel.HARD) {
            this.maxHints = 1;
        }
    }

    /**
     * Getter for puzzleId
     * @return puzzleId the ID of the puzzle this hint is for
     */
    public String getPuzzleId() {
        return puzzleId;
    }

    /**
     * Setter for puzzleId
     * @param puzzleId the ID of the puzzle this hint is for
     */
    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    /**
     * Getter for hintsUsed
     * @return hintsUsed the number of hints already used
     */
    public int getHintsUsed() {
        return hintsUsed;
    }

    /**
     * Setter for hintsUsed
     * @param hintsUsed the number of hints that have been used
     */
    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    /**
     * Getter for hintText
     * @return hintText the text of the hint
     */
    public String getHintText() {
        return hintText;
    }

    /**
     * Setter for hintText
     * @param hintText the text of the hint
     */
    public void setHintText(String hintText) {
        this.hintText = hintText;
    }
}