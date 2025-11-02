package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for WordPuzzleGame
 * Each test method contains exactly one assertion
 * CORRECTED to match actual WordPuzzleGame implementation
 */
public class WordPuzzleGameTest {
    
    private WordPuzzleGame wordGame;
    private Map<String, Object> puzzleData;
    
    @Before
    public void setUp() {
        wordGame = new WordPuzzleGame();
        wordGame.setPuzzleType("RIDDLE");
        puzzleData = createTestWordPuzzleData();
        wordGame.initialize(puzzleData);
    }
    
    private Map<String, Object> createTestWordPuzzleData() {
        Map<String, Object> data = new HashMap<>();
        data.put("prompt", "What has keys but no locks?");
        data.put("answer", "KEYBOARD");
        data.put("category", "Technology");
        data.put("maxAttempts", 3);
        
        List<String> hints = new ArrayList<>();
        hints.add("It's used with computers");
        hints.add("You type on it");
        data.put("hints", hints);
        
        return data;
    }
    
    // ===== initialize() =====
    
    @Test
    public void testInitializeSetsUpGame() {
        wordGame.initialize(puzzleData);
        assertFalse(wordGame.isGameOver());
    }
    
    @Test
    public void testInitializeDoesNotSetGameOverState() {
        assertFalse(wordGame.isGameOver());
    }
    
    // ===== getGameType() =====
    
    @Test
    public void testGetGameTypeReturnsRiddle() {
        assertEquals("RIDDLE", wordGame.getGameType());
    }
    
    @Test
    public void testSetPuzzleTypeSetsCipher() {
        wordGame.setPuzzleType("CIPHER");
        assertEquals("CIPHER", wordGame.getGameType());
    }
    
    @Test
    public void testSetPuzzleTypeSetsAnagram() {
        wordGame.setPuzzleType("ANAGRAM");
        assertEquals("ANAGRAM", wordGame.getGameType());
    }
    
    // ===== processInput() - Correct Answer =====
    
    @Test
    public void testProcessInputWithCorrectAnswerReturnsTrue() {
        assertTrue(wordGame.processInput("KEYBOARD"));
    }
    
    @Test
    public void testProcessInputWithCorrectAnswerSetsGameOver() {
        wordGame.processInput("KEYBOARD");
        assertTrue(wordGame.isGameOver());
    }
    
    @Test
    public void testProcessInputIsCaseInsensitive() {
        assertTrue(wordGame.processInput("keyboard"));
    }
    
    @Test
    public void testProcessInputWithLeadingWhitespace() {
        assertTrue(wordGame.processInput("  KEYBOARD  "));
    }
    
    // ===== processInput() - Wrong Answer =====
    
    @Test
    public void testProcessInputWithIncorrectAnswerReturnsTrue() {
        assertTrue(wordGame.processInput("MOUSE"));
    }
    
    @Test
    public void testProcessInputWithPartialAnswer() {
        assertTrue(wordGame.processInput("KEY"));
    }
    
    @Test
    public void testProcessInputWithEmptyString() {
        assertTrue(wordGame.processInput(""));
    }
    
    // ===== processInput() - HINT command =====
    
    @Test
    public void testProcessInputWithHintCommandReturnsTrue() {
        assertTrue(wordGame.processInput("HINT"));
    }
    
    @Test
    public void testProcessInputHintCommandIsCaseInsensitive() {
        assertTrue(wordGame.processInput("hint"));
    }
    
    @Test
    public void testProcessInputHintCommandWithWhitespace() {
        assertTrue(wordGame.processInput("  HINT  "));
    }
    
    // ===== isGameOver() =====
    
    @Test
    public void testIsGameOverReturnsFalseInitially() {
        assertFalse(wordGame.isGameOver());
    }
    
    @Test
    public void testIsGameOverAfterCorrectAnswer() {
        wordGame.processInput("KEYBOARD");
        assertTrue(wordGame.isGameOver());
    }
    
    @Test
    public void testIsGameOverAfterMaxAttempts() {
        wordGame.processInput("WRONG1");
        wordGame.processInput("WRONG2");
        wordGame.processInput("WRONG3");
        assertTrue(wordGame.isGameOver());
    }
    
    // ===== getGameState() =====
    
    @Test
    public void testGetGameStateReturnsNonNull() {
        assertNotNull(wordGame.getGameState());
    }
    
    @Test
    public void testGetGameStateContainsPuzzleType() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("puzzleType"));
    }
    
    @Test
    public void testGetGameStateContainsPrompt() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("prompt"));
    }
    
    @Test
    public void testGetGameStateContainsCategory() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("category"));
    }
    
    @Test
    public void testGetGameStateContainsAttemptsUsed() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("attemptsUsed"));
    }
    
    @Test
    public void testGetGameStateContainsMaxAttempts() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("maxAttempts"));
    }
    
    @Test
    public void testGetGameStateContainsRemainingAttempts() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("remainingAttempts"));
    }
    
    @Test
    public void testGetGameStateContainsGuesses() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("guesses"));
    }
    
    @Test
    public void testGetGameStateContainsRevealedHints() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("revealedHints"));
    }
    
    @Test
    public void testGetGameStateContainsAvailableHintsCount() {
        Map<String, Object> state = wordGame.getGameState();
        assertTrue(state.containsKey("availableHintsCount"));
    }
    
    // ===== getResult() =====
    
    @Test
    public void testGetResultReturnsNonNull() {
        assertNotNull(wordGame.getResult());
    }
    
    @Test
    public void testGetResultContainsWon() {
        Map<String, Object> result = wordGame.getResult();
        assertTrue(result.containsKey("won"));
    }
    
    @Test
    public void testGetResultContainsTime() {
        Map<String, Object> result = wordGame.getResult();
        assertTrue(result.containsKey("time"));
    }
    
    @Test
    public void testGetResultContainsMoves() {
        Map<String, Object> result = wordGame.getResult();
        assertTrue(result.containsKey("moves"));
    }
    
    @Test
    public void testGetResultContainsHintsUsed() {
        Map<String, Object> result = wordGame.getResult();
        assertTrue(result.containsKey("hintsUsed"));
    }
    
    @Test
    public void testGetResultWonIsTrueAfterCorrectAnswer() {
        wordGame.processInput("KEYBOARD");
        Map<String, Object> result = wordGame.getResult();
        assertTrue((Boolean) result.get("won"));
    }
    
    @Test
    public void testGetResultWonIsFalseAfterLoss() {
        wordGame.processInput("WRONG1");
        wordGame.processInput("WRONG2");
        wordGame.processInput("WRONG3");
        Map<String, Object> result = wordGame.getResult();
        assertFalse((Boolean) result.get("won"));
    }
    
    @Test
    public void testGetResultContainsAnswerWhenLost() {
        wordGame.processInput("WRONG1");
        wordGame.processInput("WRONG2");
        wordGame.processInput("WRONG3");
        Map<String, Object> result = wordGame.getResult();
        assertTrue(result.containsKey("answer"));
    }
    
    @Test
    public void testGetResultDoesNotContainAnswerWhenWon() {
        wordGame.processInput("KEYBOARD");
        Map<String, Object> result = wordGame.getResult();
        assertFalse(result.containsKey("answer"));
    }
    
    // ===== saveState() =====
    
    @Test
    public void testSaveStateReturnsNonNull() {
        assertNotNull(wordGame.saveState());
    }
    
    @Test
    public void testSaveStateContainsPuzzleType() {
        Map<String, Object> state = wordGame.saveState();
        assertTrue(state.containsKey("puzzleType"));
    }
    
    @Test
    public void testSaveStateContainsAttemptsUsed() {
        Map<String, Object> state = wordGame.saveState();
        assertTrue(state.containsKey("attemptsUsed"));
    }
    
    @Test
    public void testSaveStateContainsGuesses() {
        wordGame.processInput("WRONG");
        Map<String, Object> state = wordGame.saveState();
        assertTrue(state.containsKey("guesses"));
    }
    
    @Test
    public void testSaveStateContainsRevealedHints() {
        wordGame.processInput("HINT");
        Map<String, Object> state = wordGame.saveState();
        assertTrue(state.containsKey("revealedHints"));
    }
    
    @Test
    public void testSaveStateContainsWon() {
        Map<String, Object> state = wordGame.saveState();
        assertTrue(state.containsKey("won"));
    }
    
    @Test
    public void testSaveStateContainsStartTime() {
        Map<String, Object> state = wordGame.saveState();
        assertTrue(state.containsKey("startTime"));
    }
    
    // ===== restoreState() =====
    
    @Test
    public void testRestoreStateRestoresAttempts() {
        wordGame.processInput("WRONG");
        Map<String, Object> saved = wordGame.saveState();
        WordPuzzleGame newGame = new WordPuzzleGame();
        newGame.setPuzzleType("RIDDLE");
        newGame.initialize(puzzleData);
        newGame.restoreState(saved);
        Map<String, Object> newState = newGame.getGameState();
        assertEquals(saved.get("attemptsUsed"), newState.get("attemptsUsed"));
    }
    
    @Test
    public void testRestoreStateRestoresGuesses() {
        wordGame.processInput("WRONG");
        Map<String, Object> saved = wordGame.saveState();
        WordPuzzleGame newGame = new WordPuzzleGame();
        newGame.initialize(puzzleData);
        newGame.restoreState(saved);
        Map<String, Object> newState = newGame.getGameState();
        assertEquals(1, ((List<?>) newState.get("guesses")).size());
    }
    
    @Test
    public void testRestoreStateRestoresPuzzleType() {
        Map<String, Object> saved = wordGame.saveState();
        WordPuzzleGame newGame = new WordPuzzleGame();
        newGame.restoreState(saved);
        assertEquals("RIDDLE", newGame.getGameType());
    }
    
    // ===== reset() =====
    
    @Test
    public void testResetClearsGameOverState() {
        wordGame.processInput("KEYBOARD");
        wordGame.reset();
        assertFalse(wordGame.isGameOver());
    }
    
    @Test
    public void testResetClearsGuesses() {
        wordGame.processInput("WRONG");
        wordGame.reset();
        Map<String, Object> state = wordGame.getGameState();
        List<?> guesses = (List<?>) state.get("guesses");
        assertTrue(guesses.isEmpty());
    }
    
    @Test
    public void testResetClearsRevealedHints() {
        wordGame.processInput("HINT");
        wordGame.reset();
        Map<String, Object> state = wordGame.getGameState();
        List<?> hints = (List<?>) state.get("revealedHints");
        assertTrue(hints.isEmpty());
    }
    
    @Test
    public void testResetResetsAttemptsUsed() {
        wordGame.processInput("WRONG");
        wordGame.reset();
        Map<String, Object> state = wordGame.getGameState();
        assertEquals(0, state.get("attemptsUsed"));
    }
    
    // ===== setHints() =====
    
    @Test
    public void testSetHintsAllowsHintRevealing() {
        List<Hint> hints = new ArrayList<>();
        hints.add(new Hint("First hint", "P001", 1));
        hints.add(new Hint("Second hint", "P001", 2));
        wordGame.setHints(hints);
        wordGame.processInput("HINT");
        Map<String, Object> state = wordGame.getGameState();
        List<?> revealedHints = (List<?>) state.get("revealedHints");
        assertFalse(revealedHints.isEmpty());
    }
    
    @Test
    public void testSetHintsUpdatesAvailableCount() {
        List<Hint> hints = new ArrayList<>();
        hints.add(new Hint("First hint", "P001", 1));
        hints.add(new Hint("Second hint", "P001", 2));
        wordGame.setHints(hints);
        Map<String, Object> state = wordGame.getGameState();
        assertEquals(2, state.get("availableHintsCount"));
    }
    
    // ===== setPuzzleId() =====
    
    @Test
    public void testSetPuzzleIdStoresId() {
        wordGame.setPuzzleId("TEST_001");
        Map<String, Object> saved = wordGame.saveState();
        assertEquals("TEST_001", saved.get("puzzleId"));
    }
}
