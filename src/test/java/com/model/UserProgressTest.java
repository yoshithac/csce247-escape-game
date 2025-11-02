package com.model;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for UserProgress entity
 * Each test method contains exactly one assertion
 */
public class UserProgressTest {
    
    private UserProgress progress;
    
    @Before
    public void setUp() {
        progress = new UserProgress("user1");
    }
    
    @Test
    public void testDefaultConstructorInitializesCompletedPuzzles() {
        UserProgress emptyProgress = new UserProgress();
        assertNotNull(emptyProgress.getCompletedPuzzles());
    }
    
    @Test
    public void testDefaultConstructorInitializesPuzzleScores() {
        UserProgress emptyProgress = new UserProgress();
        assertNotNull(emptyProgress.getPuzzleScores());
    }
    
    @Test
    public void testDefaultConstructorSetsTotalScoreToZero() {
        UserProgress emptyProgress = new UserProgress();
        assertEquals(0, emptyProgress.getTotalScore());
    }
    
    @Test
    public void testDefaultConstructorCreatesEmptyCompletedPuzzles() {
        UserProgress emptyProgress = new UserProgress();
        assertTrue(emptyProgress.getCompletedPuzzles().isEmpty());
    }
    
    @Test
    public void testConstructorWithUserIdSetsUserId() {
        assertEquals("user1", progress.getUserId());
    }
    
    @Test
    public void testConstructorWithUserIdInitializesCompletedPuzzles() {
        assertNotNull(progress.getCompletedPuzzles());
    }
    
    @Test
    public void testAddCompletedPuzzleMarksPuzzleAsCompleted() {
        progress.addCompletedPuzzle("puzzle1", 100);
        assertTrue(progress.isPuzzleCompleted("puzzle1"));
    }
    
    @Test
    public void testAddCompletedPuzzleUpdatesTotalScore() {
        progress.addCompletedPuzzle("puzzle1", 100);
        assertEquals(100, progress.getTotalScore());
    }
    
    @Test
    public void testAddCompletedPuzzleIncrementsCount() {
        progress.addCompletedPuzzle("puzzle1", 100);
        assertEquals(1, progress.getCompletedCount());
    }
    
    @Test
    public void testAddCompletedPuzzleStoresScore() {
        progress.addCompletedPuzzle("puzzle1", 100);
        assertEquals(Integer.valueOf(100), progress.getPuzzleScores().get("puzzle1"));
    }
    
    @Test
    public void testAddCompletedPuzzleDoesNotAddDuplicates() {
        progress.addCompletedPuzzle("puzzle1", 100);
        progress.addCompletedPuzzle("puzzle1", 150);
        assertEquals(1, progress.getCompletedCount());
    }
    
    @Test
    public void testAddCompletedPuzzleDoesNotUpdateScoreForDuplicate() {
        progress.addCompletedPuzzle("puzzle1", 100);
        progress.addCompletedPuzzle("puzzle1", 150);
        assertEquals(100, progress.getTotalScore());
    }
    
    @Test
    public void testAddMultipleCompletedPuzzlesIncrementsCount() {
        progress.addCompletedPuzzle("puzzle1", 100);
        progress.addCompletedPuzzle("puzzle2", 75);
        progress.addCompletedPuzzle("puzzle3", 50);
        assertEquals(3, progress.getCompletedCount());
    }
    
    @Test
    public void testAddMultipleCompletedPuzzlesUpdatesTotalScore() {
        progress.addCompletedPuzzle("puzzle1", 100);
        progress.addCompletedPuzzle("puzzle2", 75);
        progress.addCompletedPuzzle("puzzle3", 50);
        assertEquals(225, progress.getTotalScore());
    }
    
    @Test
    public void testIsPuzzleCompletedReturnsFalseForUncompletedPuzzle() {
        assertFalse(progress.isPuzzleCompleted("puzzle1"));
    }
    
    @Test
    public void testSaveGameStateSetsCurrentPuzzleId() {
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("level", 5);
        progress.saveGameState("puzzle1", gameState);
        assertEquals("puzzle1", progress.getCurrentPuzzleId());
    }
    
    @Test
    public void testSaveGameStateSavesGameState() {
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("level", 5);
        progress.saveGameState("puzzle1", gameState);
        assertEquals(gameState, progress.getGameState());
    }
    
    @Test
    public void testSaveGameStateSetsHasGameInProgress() {
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("level", 5);
        progress.saveGameState("puzzle1", gameState);
        assertTrue(progress.hasGameInProgress());
    }
    
    @Test
    public void testClearGameStateClearsCurrentPuzzleId() {
        Map<String, Object> gameState = new HashMap<>();
        progress.saveGameState("puzzle1", gameState);
        progress.clearGameState();
        assertNull(progress.getCurrentPuzzleId());
    }
    
    @Test
    public void testClearGameStateClearsGameState() {
        Map<String, Object> gameState = new HashMap<>();
        progress.saveGameState("puzzle1", gameState);
        progress.clearGameState();
        assertNull(progress.getGameState());
    }
    
    @Test
    public void testClearGameStateClearsHasGameInProgress() {
        Map<String, Object> gameState = new HashMap<>();
        progress.saveGameState("puzzle1", gameState);
        progress.clearGameState();
        assertFalse(progress.hasGameInProgress());
    }
    
    @Test
    public void testHasGameInProgressReturnsFalseWhenNoPuzzleId() {
        Map<String, Object> gameState = new HashMap<>();
        progress.setGameState(gameState);
        progress.setCurrentPuzzleId(null);
        assertFalse(progress.hasGameInProgress());
    }
    
    @Test
    public void testHasGameInProgressReturnsFalseWhenNoGameState() {
        progress.setCurrentPuzzleId("puzzle1");
        progress.setGameState(null);
        assertFalse(progress.hasGameInProgress());
    }
    
    @Test
    public void testGetCompletedCountReturnsCorrectCount() {
        progress.addCompletedPuzzle("puzzle1", 100);
        progress.addCompletedPuzzle("puzzle2", 75);
        assertEquals(2, progress.getCompletedCount());
    }
    
    @Test
    public void testSetUserId() {
        progress.setUserId("newUser");
        assertEquals("newUser", progress.getUserId());
    }
    
    @Test
    public void testSetTotalScore() {
        progress.setTotalScore(500);
        assertEquals(500, progress.getTotalScore());
    }
}
