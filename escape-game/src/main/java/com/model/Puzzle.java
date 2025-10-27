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
    
<<<<<<< HEAD
=======
    /**
     * Constructs a new {@code Puzzle} instance with the specified properties.
     *
     * @param puzzleId the unique identifier of the puzzle
     * @param puzzleType the type of puzzle (e.g., MAZE, MATCHING, etc.)
     * @param difficulty the difficulty level (e.g., EASY, MEDIUM, HARD)
     * @param title the title or display name of the puzzle
     * @param description a short text describing the puzzle
     * @param data a map of puzzle-specific configuration data
     */
>>>>>>> main
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
<<<<<<< HEAD
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
    
=======

    /**
     * Returns the unique identifier of this puzzle.
     *
     * @return the puzzle ID
     */
    public String getPuzzleId() { 
        return puzzleId; 
    }

    /**
     * Sets the unique identifier for this puzzle.
     *
     * @param puzzleId the new puzzle ID
     */
    public void setPuzzleId(String puzzleId) { 
        this.puzzleId = puzzleId; 
    }

    /**
     * Returns the type of this puzzle.
     *
     * @return the puzzle type (e.g., MAZE, MATCHING)
     */
    public String getPuzzleType() { 
        return puzzleType; 
    }

    /**
     * Sets the type of this puzzle.
     *
     * @param puzzleType the puzzle type (e.g., MAZE, MATCHING)
     */
    public void setPuzzleType(String puzzleType) { 
        this.puzzleType = puzzleType; 
    }

    /**
     * Returns the difficulty level of the puzzle.
     *
     * @return the difficulty (e.g., EASY, MEDIUM, HARD)
     */
    public String getDifficulty() { 
        return difficulty; 
    }

    /**
     * Sets the difficulty level of the puzzle.
     *
     * @param difficulty the difficulty level (e.g., EASY, MEDIUM, HARD)
     */
    public void setDifficulty(String difficulty) { 
        this.difficulty = difficulty; 
    }

    /**
     * Returns the title of the puzzle.
     *
     * @return the puzzle title
     */
    public String getTitle() { 
        return title; 
    }

    /**
     * Sets the title of the puzzle.
     *
     * @param title the puzzle title
     */
    public void setTitle(String title) { 
        this.title = title; 
    }

    /**
     * Returns the description of the puzzle.
     *
     * @return the puzzle description
     */
    public String getDescription() { 
        return description; 
    }

    /**
     * Sets the description of the puzzle.
     *
     * @param description the puzzle description
     */
    public void setDescription(String description) { 
        this.description = description; 
    }

    /**
     * Returns the puzzle-specific initialization data.
     *
     * @return a map of data used to configure the puzzle
     */
    public Map<String, Object> getData() { 
        return data; 
    }

    /**
     * Sets the puzzle-specific initialization data.
     *
     * @param data a map containing configuration data for the puzzle
     */
    public void setData(Map<String, Object> data) { 
        this.data = data; 
    }

    /**
     * Returns a formatted string representation of this puzzle.
     * The format is: {@code [Type] Title - Difficulty (ID)}.
     *
     * @return a string representation of this puzzle
     */
>>>>>>> main
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", puzzleType, title, difficulty, puzzleId);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> main
