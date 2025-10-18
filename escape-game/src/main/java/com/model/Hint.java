package com.model;

/*
 * This class represents a hint that can be given to players
 * @author We're Getting an A
 */
public class Hint extends GameManager {
    private int hintsUsed;
    private int maxHints;
    private String puzzleId;
    private String hintText;

    public Hint(String puzzleId, String hintText, int maxHints) {
        this.puzzleId = puzzleId;
        this.hintText = hintText;
        this.maxHints = maxHints;
        this.hintsUsed = 0;
    }

    public String provideHint(String puzzleId) {
        if (canUseHint()) {
            hintsUsed++;
            return hintText;
        }
        return "No more hints available.";
    }

    public boolean canUseHint() {
        return hintsUsed < maxHints;
    }

    public void resetHints() {
        hintsUsed = 0;
    }

    public void setMaxHints(int maxHints) {
        if (getDifficulty() == DifficultyLevel.EASY) {
            this.maxHints = 5;
        } else if (getDifficulty() == DifficultyLevel.MEDIUM) {
            this.maxHints = 3;
        } else if (getDifficulty() == DifficultyLevel.HARD) {
            this.maxHints = 1;
        }
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }
}
