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
    public String getPuzzleId() { 
        return puzzleId; 
    }
    
    public void setPuzzleId(String puzzleId) { 
        this.puzzleId = puzzleId; 
    }
    
    public String getPuzzleType() { 
        return puzzleType; 
    }
    
    public void setPuzzleType(String puzzleType) { 
        this.puzzleType = puzzleType; 
    }
    
    public String getDifficulty() { 
        return difficulty; 
    }
    
    public void setDifficulty(String difficulty) { 
        this.difficulty = difficulty; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public Map<String, Object> getData() { 
        return data; 
    }
    
    public void setData(Map<String, Object> data) { 
        this.data = data; 
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", puzzleType, title, difficulty, puzzleId);
    }
}
