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
     * Constructor with all fields
     * @param hintText
     * @param puzzleId
     * @param hintPriority
     */
    public Hint(String hintText, String puzzleId, int hintPriority) {
        this.hintText = hintText;
        this.puzzleId = puzzleId;
        this.hintPriority = hintPriority;
    }
    
    // Getters and Setters
    public String getHintText() { 
        return hintText; 
    }
    
    /**
     * Set hint text
     * @param hintText
     */
    public void setHintText(String hintText) { 
        this.hintText = hintText; 
    }
    
    /**
     * Get puzzle ID
     * @return puzzleId
     */
    public String getPuzzleId() { 
        return puzzleId; 
    }
    
    /**
     * Set puzzle ID
     * @param puzzleId
     */
    public void setPuzzleId(String puzzleId) { 
        this.puzzleId = puzzleId; 
    }
    
    /**
     * Get hint priority
     * @return
     */
    public int getHintPriority() { 
        return hintPriority; 
    }
    
    /**
     * Set hint priority
     * @param hintPriority
     */
    public void setHintPriority(int hintPriority) { 
        this.hintPriority = hintPriority; 
    }
    
    /**
     * Compare hints by priority
     * @param other Other hint
     * @return comparison result
     * @Override
     */
    public int compareTo(Hint other) {
        return Integer.compare(this.hintPriority, other.hintPriority);
    }
    
    /**
     * String representation
     * @return String representation
     * @Override    
     */
    public String toString() {
        return String.format("[Priority %d] %s", hintPriority, hintText);
    }
}
