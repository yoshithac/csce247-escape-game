package com.model;

import java.util.Map;

/**
 * Puzzle entity representing a game puzzle
 * Contains puzzle metadata and data for initialization
 */
public class Puzzle {
    private String puzzleId;
    private String puzzleType;  // MAZE, MATCHING, CIPHER, ANAGRAM, RIDDLE
    private String difficulty;  // EASY, MEDIUM, HARD
    private String title;
    private String description;
    private Map<String, Object> data;  // Puzzle-specific data
    
    public Puzzle() {}
    
    /**
     *  Constructor with all fields
     * @param puzzleId
     * @param puzzleType
     * @param difficulty
     * @param title
     * @param description
     * @param data
     */
    public Puzzle(String puzzleId, String puzzleType, String difficulty, String title, 
                  String description, Map<String, Object> data) {
        this.puzzleId = puzzleId;
        this.puzzleType = puzzleType;
        this.difficulty = difficulty;
        this.title = title;
        this.description = description;
        this.data = data;
    }
    
    // Getters and Setters
    /**
     * Get puzzle ID
     * @return String puzzleId
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
     * Get puzzle type
     * @return String puzzleType
     */
    public String getPuzzleType() { 
        return puzzleType; 
    }
    
    /**
     * Set puzzle type
     * @param puzzleType
     */
    public void setPuzzleType(String puzzleType) { 
        this.puzzleType = puzzleType; 
    }
    
    /**
     * Get difficulty level
     * @return String difficulty
     */
    public String getDifficulty() { 
        return difficulty; 
    }
    
    /**
     * Set difficulty level
     * @param difficulty
     */
    public void setDifficulty(String difficulty) { 
        this.difficulty = difficulty; 
    }
    
    /**
     * Get title
     * @return String title
     */
    public String getTitle() { 
        return title; 
    }
    
    /**
     * Set title
     * @param title
     */
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    /**
     * Get description
     * @return String description
     */
    public String getDescription() { 
        return description; 
    }
    
    /**
     * Set description
     * @param description
     */
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    /**
     * Get puzzle data
     * @return Map<String, Object> data
     */
    public Map<String, Object> getData() { 
        return data; 
    }
    
    /**
     * Set puzzle data
     * @param data
     */
    public void setData(Map<String, Object> data) { 
        this.data = data; 
    }
    
    /**
     * Override toString for easy debugging
     * @return String representation of Puzzle
     * @Override
     */
    public String toString() {
        return String.format("[%s] %s - %s (%s)", puzzleType, title, difficulty, puzzleId);
    }
}