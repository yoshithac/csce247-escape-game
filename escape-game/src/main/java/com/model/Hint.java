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
    
    public Hint(String hintText, String puzzleId, int hintPriority) {
        this.hintText = hintText;
        this.puzzleId = puzzleId;
        this.hintPriority = hintPriority;
    }
    
    // Getters and Setters
    public String getHintText() { 
        return hintText; 
    }
    
    public void setHintText(String hintText) { 
        this.hintText = hintText; 
    }
    
    public String getPuzzleId() { 
        return puzzleId; 
    }
    
    public void setPuzzleId(String puzzleId) { 
        this.puzzleId = puzzleId; 
    }
    
    public int getHintPriority() { 
        return hintPriority; 
    }
    
    public void setHintPriority(int hintPriority) { 
        this.hintPriority = hintPriority; 
    }
    
    @Override
    public int compareTo(Hint other) {
        return Integer.compare(this.hintPriority, other.hintPriority);
    }
    
    @Override
    public String toString() {
        return String.format("[Priority %d] %s", hintPriority, hintText);
    }
}
