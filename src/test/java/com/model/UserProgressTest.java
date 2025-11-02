package com.model;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for UserProgress.
 * Verifies construction, adding completed puzzles, scoring,
 * saved-game behaviour, and edge cases such as duplicates and partial saves.
 */
public class UserProgressTest {

    /**
     * Default constructor should create empty lists/maps and zero totalScore.
     */
    @Test
    public void testDefaultConstructor_initializesEmptyCollectionsAndZeroScore() {
        UserProgress up = new UserProgress();

        // Concrete expectations: newly-created progress has zero completed puzzles and score 0
        assertNotNull("completedPuzzles list should not be null", up.getCompletedPuzzles());
        assertNotNull("puzzleScores map should not be null", up.getPuzzleScores());
        assertEquals("totalScore should be 0 by default", 0, up.getTotalScore());
        assertEquals("completed count should be 0 by default", 0, up.getCompletedCount());
        assertFalse("no game in progress by default", up.hasGameInProgress());
    }

    /**
     * Parameterized constructor should store provided userId.
     */
    @Test
    public void testParameterizedConstructor_setsUserId() {
        UserProgress up = new UserProgress("user123");

        assertEquals("UserId should match constructor argument", "user123", up.getUserId());
        // other fields still default
        assertEquals("initial totalScore should be 0", 0, up.getTotalScore());
    }

    /**
     * Adding a new completed puzzle should add to list, update scores map, and increment totalScore.
     */
    @Test
    public void testAddCompletedPuzzle_addsNewPuzzleAndUpdatesScore() {
        UserProgress up = new UserProgress("u1");

        // Concrete actions
        up.addCompletedPuzzle("puzzleA", 50);
        up.addCompletedPuzzle("puzzleB", 30);

        // Concrete expected outputs
        assertTrue("puzzleA should be marked completed", up.isPuzzleCompleted("puzzleA"));
        assertTrue("puzzleB should be marked completed", up.isPuzzleCompleted("puzzleB"));
        assertEquals("completed count should be 2 after adding two unique puzzles", 2, up.getCompletedCount());
        assertEquals("puzzleA should have stored score 50", Integer.valueOf(50), up.getPuzzleScores().get("puzzleA"));
        assertEquals("totalScore should equal sum of 50 + 30 = 80", 80, up.getTotalScore());
    }

    /**
     * Adding the same puzzle twice should not duplicate entry or increase the total score.
     */
    @Test
    public void testAddCompletedPuzzle_duplicateDoesNotIncreaseScoreOrCount() {
        UserProgress up = new UserProgress("u2");

        up.addCompletedPuzzle("pX", 20);
        // Attempt to add duplicate with a different score — should be ignored by implementation
        up.addCompletedPuzzle("pX", 999);

        // Expectations: only one completed entry, score remains the original 20
        assertEquals("completed count should remain 1 after duplicate add", 1, up.getCompletedCount());
        assertEquals("pX score should remain as first provided (20)", Integer.valueOf(20), up.getPuzzleScores().get("pX"));
        assertEquals("totalScore should remain 20 after duplicate add", 20, up.getTotalScore());
    }

    /**
     * hasGameInProgress should be false when only puzzleId is set (state null) or only state is set (puzzleId null).
     */
    @Test
    public void testHasGameInProgress_falseWhenOnlyPuzzleIdOrOnlyStatePresent() {
        UserProgress up1 = new UserProgress();
        // simulate only puzzle id set
        up1.setCurrentPuzzleId("incompletePuzzle");
        up1.setGameState(null);
        assertFalse("hasGameInProgress should be false when state is null even if puzzleId present", up1.hasGameInProgress());

        UserProgress up2 = new UserProgress();
        // simulate only state set
        Map<String, Object> state = new HashMap<>();
        state.put("progress", "some");
        up2.setCurrentPuzzleId(null);
        up2.setGameState(state);
        assertFalse("hasGameInProgress should be false when puzzleId is null even if state present", up2.hasGameInProgress());
    }

    /**
     * saveGameState should store both puzzleId and state; hasGameInProgress becomes true.
     */
    @Test
    public void testSaveGameState_andHasGameInProgress_trueThenClear() {
        UserProgress up = new UserProgress("u3");
        Map<String, Object> state = new HashMap<>();
        state.put("pos", 5);
        state.put("score", 10);

        up.saveGameState("currentPuzzle", state);

        // After saving, both fields should be set and hasGameInProgress true
        assertEquals("currentPuzzleId should be saved", "currentPuzzle", up.getCurrentPuzzleId());
        assertEquals("saved state map should be accessible", state, up.getGameState());
        assertTrue("hasGameInProgress should be true after saving both puzzleId and state", up.hasGameInProgress());

        // Clearing should remove both and return hasGameInProgress to false
        up.clearGameState();
        assertNull("currentPuzzleId should be null after clearGameState", up.getCurrentPuzzleId());
        assertNull("gameState should be null after clearGameState", up.getGameState());
        assertFalse("hasGameInProgress should be false after clearing saved state", up.hasGameInProgress());
    }

    /**
     * saveGameState should overwrite a previous saved state with new values.
     */
    @Test
    public void testSaveGameState_overwritesPreviousState() {
        UserProgress up = new UserProgress("u4");
        Map<String, Object> state1 = new HashMap<>();
        state1.put("step", 1);
        up.saveGameState("p1", state1);

        Map<String, Object> state2 = new HashMap<>();
        state2.put("step", 2);
        up.saveGameState("p2", state2);

        // Expectations: latest values are stored
        assertEquals("currentPuzzleId should be overwritten to p2", "p2", up.getCurrentPuzzleId());
        assertEquals("gameState should be overwritten to state2", state2, up.getGameState());
        assertTrue("hasGameInProgress should be true after overwrite", up.hasGameInProgress());
    }
}
