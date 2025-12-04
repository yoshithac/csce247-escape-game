package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unified word puzzle game for CIPHER, ANAGRAM, and RIDDLE
 * Uses puzzleType field for different prompting
 * Includes hint system and save/resume functionality
 */
public class WordPuzzleGame implements PuzzleGame {
    private String puzzleType;  // CIPHER, ANAGRAM, or RIDDLE
    private String puzzleId;
    private String prompt;
    private String answer;
    private String category;
    private int maxAttempts;
    private int attemptsUsed;
    private List<String> guesses;
    private List<Hint> hints;
    private List<String> revealedHints;
    private boolean won;
    private long startTime;
    
    /**
     * Initialize word puzzle game with puzzle data
     * @param puzzleData Map of puzzle data
     * @Override
     */
    public void initialize(Map<String, Object> puzzleData) {
        this.prompt = (String) puzzleData.get("prompt");
        this.answer = ((String) puzzleData.get("answer")).toUpperCase();
        this.category = (String) puzzleData.get("category");
        this.maxAttempts = ((Number) puzzleData.get("maxAttempts")).intValue();
        this.attemptsUsed = 0;
        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.revealedHints = new ArrayList<>();
        this.won = false;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Process player input for guessing or hint request
     * @param input Player input command
     * @return boolean indicating if input was processed
     * @Override
     */
    public boolean processInput(String input) {
        input = input.trim().toUpperCase();
        
        // Check for hint request
        if (input.equals("HINT")) {
            revealNextHint();
            return true;
        }
        
        // Check for answer
        if (input.equals(answer)) {
            won = true;
            return true;
        }
        
        // Wrong answer
        if (!guesses.contains(input)) {
            guesses.add(input);
        }
        attemptsUsed++;
        
        return true;
    }
    
    /**
     * Check if game is over (won or max attempts reached)
     * @return true if game over
     * @Override
     */
    public boolean isGameOver() {
        return won || attemptsUsed >= maxAttempts;
    }
    
    /**
     * Get current game state for display
     * @return Map of game state
     * @Override
     */
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("puzzleType", puzzleType);
        state.put("prompt", prompt);
        state.put("category", category);
        state.put("attemptsUsed", attemptsUsed);
        state.put("maxAttempts", maxAttempts);
        state.put("remainingAttempts", maxAttempts - attemptsUsed);
        state.put("guesses", guesses);
        state.put("revealedHints", revealedHints);
        state.put("availableHintsCount", hints.size() - revealedHints.size());
        return state;
    }
    
    /**
     * Get game type (CIPHER, ANAGRAM, RIDDLE)
     * @return String game type
     * @Override
     */
    public String getGameType() {
        return puzzleType;
    }
    
    /**
     * Get result summary
     * @return Map of result data
     * @Override
     */
    public Map<String, Object> getResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("won", won);
        result.put("time", System.currentTimeMillis() - startTime);
        result.put("moves", attemptsUsed);
        result.put("hintsUsed", revealedHints.size());
        if (!won) {
            result.put("answer", answer);
        }
        return result;
    }
    
    /**
     * Reset game to initial state
     * @Override
     */
    public void reset() {
        attemptsUsed = 0;
        guesses.clear();
        revealedHints.clear();
        won = false;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Save current game state for persistence
     * @return Map of saved state
     * @Override
     */
    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("puzzleType", puzzleType);
        state.put("puzzleId", puzzleId);
        state.put("prompt", prompt);
        state.put("answer", answer);
        state.put("category", category);
        state.put("maxAttempts", maxAttempts);
        state.put("attemptsUsed", attemptsUsed);
        state.put("guesses", guesses);
        state.put("revealedHints", revealedHints);
        state.put("won", won);
        state.put("startTime", startTime);
        return state;
    }
    
    /**
     * Restore game state from saved data
     * @param savedState Map of saved state
     * @Override
     */
    public void restoreState(Map<String, Object> savedState) {
        this.puzzleType = (String) savedState.get("puzzleType");
        this.puzzleId = (String) savedState.get("puzzleId");
        this.prompt = (String) savedState.get("prompt");
        this.answer = (String) savedState.get("answer");
        this.category = (String) savedState.get("category");
        this.maxAttempts = ((Number) savedState.get("maxAttempts")).intValue();
        this.attemptsUsed = ((Number) savedState.get("attemptsUsed")).intValue();
        this.guesses = (List<String>) savedState.get("guesses");
        this.revealedHints = (List<String>) savedState.get("revealedHints");
        this.won = (Boolean) savedState.get("won");
        this.startTime = ((Number) savedState.get("startTime")).longValue();
        this.hints = new ArrayList<>();  // Will be set by setHints()
    }
    
    /**
     * Set hints for this puzzle (called by GameController)
     * @param hints List of hints sorted by priority
     */
    public void setHints(List<Hint> hints) {
        this.hints = hints;
    }
    
    /**
     * Set puzzle type (CIPHER, ANAGRAM, RIDDLE)
     * @param puzzleType Type from puzzle data
     */
    public void setPuzzleType(String puzzleType) {
        this.puzzleType = puzzleType;
    }
    
    /**
     * Set puzzle ID for tracking
     * @param puzzleId Puzzle identifier
     */
    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }
    
    /**
     * Reveal next available hint
     */
    private void revealNextHint() {
        int revealedCount = revealedHints.size();
        if (revealedCount < hints.size()) {
            Hint nextHint = hints.get(revealedCount);
            revealedHints.add(nextHint.getHintText());
        }
    }
}