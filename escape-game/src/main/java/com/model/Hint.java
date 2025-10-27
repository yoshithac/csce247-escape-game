package com.model;

/**
 * Hint entity for puzzle hints
 * Hints are ordered by priority (1 = first, 2 = second, etc.)
 */
public class Hint implements Comparable<Hint> {
    private String hintText;
    private String puzzleId;
    private int hintPriority;
    
    public Hint() {}
    /**
     * Constructs a new {@code Hint} with the specified text, puzzle ID, and priority.
     *
     * @param hintText the text of the hint
     * @param puzzleId the ID of the accociated puzzle
     * @param hintPriority the priority order of the hint
     */

    public Hint(String hintText, String puzzleId, int hintPriority) {
        this.hintText = hintText;
        this.puzzleId = puzzleId;
        this.hintPriority = hintPriority;
    }
    
    // Getters and Setters

    /**
     * Returns the text of this hint.
     *
     * @return the hint text
     */
    public String getHintText() { 
        return hintText; 
    }

    /**
     * Sets the text of this hint.
     *
     * @param hintText the new hint text
     */
    public void setHintText(String hintText) { 
        this.hintText = hintText; 
    }

    /**
     * Returns the ID of the puzzle this hint is associated with.
     *
     * @return the puzzle ID
     */
    public String getPuzzleId() { 
        return puzzleId; 
    }

    /**
     * Sets the puzzle ID this hint is associated with.
     *
     * @param puzzleId the puzzle ID
     */
    public void setPuzzleId(String puzzleId) { 
        this.puzzleId = puzzleId; 
    }

    /**
     * Returns the priority of this hint.
     *
     * @return the hint priority 
     */
    public int getHintPriority() { 
        return hintPriority; 
    }

    /**
     * Sets the priority order of this hint.
     *
     * @param hintPriority the hint priority 
     */
    public void setHintPriority(int hintPriority) { 
        this.hintPriority = hintPriority; 
    }

    /**
     * Compares this hint to another based on priority order.
     * Lower priority numbers come before higher ones.
     *
     * @param other the other {@code Hint} to compare with
     * @return a negative integer, zero, or a positive integer if this hint's
     * priority is less than, equal to, or greater than the other hint's priority
     */
    @Override
    public int compareTo(Hint other) {
        return Integer.compare(this.hintPriority, other.hintPriority);
    }

    /**
     * Returns a formatted string representation of this hint,
     * including its priority and text.
     *
     * @return a string describing the hint
     */
    @Override
    public String toString() {
        return String.format("[Priority %d] %s", hintPriority, hintText);
    }
}