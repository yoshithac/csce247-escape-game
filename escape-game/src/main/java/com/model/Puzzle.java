package com.model;

/**
 * Represents a puzzle in the escape game
 * @author We're Getting an A
 */
public class Puzzle {
    private String puzzleId;
    private String type;
    private String prompt;
    private String answer;
    private String description;
    private DifficultyLevel difficulty;
    private boolean isCompleted;
    private int maxAttempts;

    /**
     * Constructs a new puzzle with specified parameters
     * @param puzzleId The unique ID for the puzzle
     * @param type The type of puzzle
     * @param prompt The puzzle promt or question
     * @param answer The correct answer to the puzzle
     * @param description Description for the puzzle
     * @param difficulty The difficulty level of the puzzle
     * @param maxAttempts Maximum number of attempts allowed
     */
    public Puzzle(String puzzleId, String type, String prompt, String answer, String description, DifficultyLevel difficulty, int maxAttempts){
        this.puzzleId = puzzleId;
        this.type = type;
        this.prompt = prompt;
        this.answer = answer;
        this.description = description;
        this.difficulty = difficulty != null ? difficulty : DifficultyLevel.MEDIUM;
        this.maxAttempts = Math.max(1, maxAttempts);
        this.isCompleted = false;

    }

    /**
     * Plays the puzzle
     */
    public void playPuzzle() {
        
    }

    /**
     * Checks if the puzzle is completed
     * @return true if completed, false otherwise
     */
    public boolean checkComplete() {
        return isCompleted;
    }

    /**
     * Provides a hint for the puzzle
     * @return A hint string
     */
    public String getHint() {
        return "Hint: " + prompt;
    }

    /**
     * Resets the puzzle to its initial state
     */
    public void reset() {
       isCompleted = false;
    }


    /**
     * Gets puzzle ID
     * @return The puzzle ID
     */
    public String getPuzzleId() { 
        return puzzleId; 
    }

    /**
     * Gets the prompt of the puzzle
     * @return The prompt for puzzle
     */
    public String getPrompt() { 
        return prompt; 
    }

    /**
     * Gets the answer of the puzzle
     * @return The answer for puzzle
     */
    public String getAnswer() { 
        return answer; 
    }

    /**
     * Gets the completion status of the puzzle
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() { 
        return isCompleted; 
    }

    /**
     * Sets the completion status of the puzzle
     * @param completed The completion status to set
     */
    public void setCompleted(boolean completed) { 
        this.isCompleted = completed; 
    }

    /**
     * Gets the difficulty level of the puzzle
     * @return The difficulty level
     */
    public DifficultyLevel getDifficulty() { 
        return difficulty;
    }

    /**
     * Gets the maximum number of attempts for the puzzle
     * @return The maximum attempts
     */
    public int getMaxAttempts() { 
        return maxAttempts; 
    }

    /**
     * Sets the maximum number of attempts for the puzzle
     * @param attempts The maximum attempts to set
     */
    public void setMaxAttempts(int attempts) { 
        this.maxAttempts = Math.max(1, attempts); 
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
