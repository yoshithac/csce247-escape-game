package com.model;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for the PuzzleGame interface using a small dummy implementation.
 * Since PuzzleGame is an interface, these tests provide a simple
 * {@code DummyPuzzleGame} implementation to verify the expected contract
 * - initialize/populate game state
 * - processInput behaviour
 * - save/restore state
 * - reset behaviour
 * - result and game type accessors
 */
public class PuzzleGameTest {

    /**
     * Minimal test implementation of PuzzleGame used by the tests below.
     */
    static class DummyPuzzleGame implements PuzzleGame {
        private Map<String, Object> state = new HashMap<>();
        private boolean gameOver = false;
        private final String type;

        DummyPuzzleGame(String type) {
            this.type = type;
        }

        @Override
        public void initialize(Map<String, Object> puzzleData) {
            state.clear();
            if (puzzleData != null) state.putAll(puzzleData);
            // mark not-over when initialized
            gameOver = false;
        }

        @Override
        public boolean processInput(String input) {
            if (input == null) return false;
            // treat "win" as a winning move; any non empty string is valid
            state.put("lastInput", input);
            if ("win".equalsIgnoreCase(input)) {
                gameOver = true;
                // store a simple result when winning
                state.put("won", true);
                state.put("moves", ((Integer) state.getOrDefault("moves", 0)) + 1);
            } else {
                state.put("won", false);
                state.put("moves", ((Integer) state.getOrDefault("moves", 0)) + 1);
            }
            return true;
        }

        @Override
        public boolean isGameOver() {
            return gameOver;
        }

        @Override
        public Map<String, Object> getGameState() {
            return state;
        }

        @Override
        public String getGameType() {
            return type;
        }

        @Override
        public Map<String, Object> getResult() {
            Map<String, Object> res = new HashMap<>();
            res.put("won", state.getOrDefault("won", false));
            res.put("moves", state.getOrDefault("moves", 0));
            return res;
        }

        @Override
        public void reset() {
            state.clear();
            gameOver = false;
        }

        @Override
        public Map<String, Object> saveState() {
            
            Map<String, Object> copy = new HashMap<>(state);
            copy.put("gameOver", gameOver);
            copy.put("gameType", type);
            return copy;
        }

        @Override
        public void restoreState(Map<String, Object> savedState) {
            state.clear();
            if (savedState != null) {
                state.putAll(savedState);
                Object go = savedState.get("gameOver");
                gameOver = go instanceof Boolean ? (Boolean) go : false;
            }
        }
    }

    @Test
    public void testInitialize_populatesGameStateAndResetsGameOver() {
        DummyPuzzleGame game = new DummyPuzzleGame("MAZE");
        Map<String, Object> init = new HashMap<>();
        init.put("width", 5);
        init.put("height", 3);

        game.initialize(init);

        assertNotNull("Game state should not be null after initialize", game.getGameState());
        assertEquals("width should be set by initialize", 5, game.getGameState().get("width"));
        assertEquals("height should be set by initialize", 3, game.getGameState().get("height"));
        assertFalse("Game should not be over immediately after initialize", game.isGameOver());
    }

    @Test
    public void testProcessInput_validAndInvalid() {
        DummyPuzzleGame game = new DummyPuzzleGame("MATCHING");
        game.initialize(null);

        // invalid input (null) returns false and does not change state
        assertFalse("Null input should be invalid", game.processInput(null));
        assertEquals("moves should be 0 after invalid input", 0, game.getGameState().getOrDefault("moves", 0));

        // valid input increments moves and sets lastInput
        assertTrue("Non-null input should be valid", game.processInput("tap"));
        assertEquals("lastInput should be recorded", "tap", game.getGameState().get("lastInput"));
        assertEquals("moves should be 1 after one valid input", 1, game.getGameState().get("moves"));

        // winning input should set gameOver to true
        assertTrue("Winning input should be accepted", game.processInput("win"));
        assertTrue("Game should be over after 'win' input", game.isGameOver());
    }

    @Test
    public void testSaveAndRestore_preservesStateAndGameOverFlag() {
        DummyPuzzleGame game = new DummyPuzzleGame("CIPHER");
        game.initialize(null);
        game.processInput("a");
        game.processInput("win"); // now game over

        Map<String, Object> saved = game.saveState();
        assertNotNull("Saved state should not be null", saved);
        assertTrue("Saved state should include gameOver flag", saved.containsKey("gameOver"));

        // create a fresh instance and restore
        DummyPuzzleGame restored = new DummyPuzzleGame("CIPHER");
        restored.restoreState(saved);

        assertEquals("Restored lastInput should match saved state", saved.get("lastInput"), restored.getGameState().get("lastInput"));
        assertEquals("Restored gameOver flag should match saved state", saved.get("gameOver"), restored.saveState().get("gameOver"));
    }

    @Test
    public void testReset_clearsStateAndGameOver() {
        DummyPuzzleGame game = new DummyPuzzleGame("ANAGRAM");
        game.initialize(null);
        game.processInput("x");
        assertTrue("moves should be > 0 after input", ((Integer) game.getGameState().get("moves")) > 0);

        game.reset();
        assertFalse("Game should not be over after reset", game.isGameOver());
        assertEquals("State should be empty after reset", 0, game.getGameState().size());
    }

    @Test
    public void testGetGameTypeAndResult_afterPlay() {
        DummyPuzzleGame game = new DummyPuzzleGame("RIDDLE");
        game.initialize(null);
        game.processInput("first");
        game.processInput("second");
        game.processInput("win");

        assertEquals("getGameType should return the game type", "RIDDLE", game.getGameType());

        Map<String, Object> result = game.getResult();
        assertNotNull("Result map should not be null", result);
        assertEquals("Result 'won' should be true after win", true, result.get("won"));
        assertEquals("Result 'moves' should equal recorded moves", game.getGameState().get("moves"), result.get("moves"));
    }
}
