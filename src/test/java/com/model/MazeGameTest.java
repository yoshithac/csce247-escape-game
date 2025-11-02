package com.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for MazeGame (tests non-getter/setter behavior)
 */
public class MazeGameTest {

    /**
     * Helper to build puzzleData for a simple maze:
     * 0 = path, 1 = wall
     *
     * Example 3x3 grid used:
     * 0 0 0
     * 1 1 0
     * 0 0 0
     *
     * start at (0,0), end at (2,2)
     */
    private Map<String, Object> makePuzzleData() {
        Map<String, Object> data = new HashMap<>();
        int width = 3;
        int height = 3;
        data.put("width", width);
        data.put("height", height);

        // grid as List<List<Number>>
        List<List<Number>> gridList = new ArrayList<>();
        gridList.add(Arrays.asList(0, 0, 0));
        gridList.add(Arrays.asList(1, 1, 0));
        gridList.add(Arrays.asList(0, 0, 0));
        data.put("grid", gridList);

        Map<String, Object> start = new HashMap<>();
        start.put("row", 0);
        start.put("col", 0);
        data.put("start", start);

        Map<String, Object> end = new HashMap<>();
        end.put("row", 2);
        end.put("col", 2);
        data.put("end", end);

        return data;
    }

    @Test
    public void testInitialize_andGetGameType_andGetGameState() {
        MazeGame game = new MazeGame();
        Map<String, Object> puzzle = makePuzzleData();
        game.initialize(puzzle);

        assertEquals("Game type should be MAZE", "MAZE", game.getGameType());

        Map<String, Object> state = game.getGameState();
        assertNotNull("State should not be null", state);
        assertTrue("State should contain maze", state.get("maze") instanceof Maze);
        assertTrue("State should contain player", state.get("player") instanceof Player);
        assertEquals("Initial moveCount should be 0", 0, ((Number) state.get("moveCount")).intValue());
    }

    @Test
    public void testProcessInput_invalidCommand_andBounds_andWalls() {
        MazeGame game = new MazeGame();
        game.initialize(makePuzzleData());

        // invalid command
        assertFalse("Invalid command should return false", game.processInput("X"));

        // moving up from start (0,0) is out of bounds
        assertFalse("Move out of bounds should return false", game.processInput("W"));

        // moving left from start is out of bounds
        assertFalse("Move out of bounds should return false", game.processInput("A"));

        // moving right from (0,0) should be allowed (to 0,1)
        assertTrue("Move right should be allowed", game.processInput("D"));

        // moving down from (0,1) goes to (1,1) which is a wall (1) -> should be false
        assertFalse("Move down into wall should be false", game.processInput("S"));
    }

    @Test
    public void testValidMoves_incrementMoveCount_andUpdatePlayer() {
        MazeGame game = new MazeGame();
        game.initialize(makePuzzleData());

        // Starting at (0,0)
        assertTrue("Right to (0,1) allowed", game.processInput("D"));
        assertTrue("Right to (0,2) allowed", game.processInput("D"));
        // from (0,2), down to (1,2) is allowed (it's 0)
        assertTrue("Down to (1,2) allowed", game.processInput("S"));
        // from (1,2), down to (2,2) is allowed -> reaching end
        assertTrue("Down to (2,2) allowed", game.processInput("S"));

        // After 4 valid moves, moveCount should be 4
        Map<String, Object> state = game.getGameState();
        assertEquals("moveCount should be 4 after moves", 4, ((Number) state.get("moveCount")).intValue());

        // Player position should be at end (2,2)
        Player p = (Player) state.get("player");
        assertEquals(2, p.row);
        assertEquals(2, p.col);

        // Game should be over
        assertTrue("Game should be over after reaching end", game.isGameOver());
    }

    @Test
    public void testGetResult_containsWonMovesAndTime() throws InterruptedException {
        MazeGame game = new MazeGame();
        game.initialize(makePuzzleData());

        // perform one move
        assertTrue(game.processInput("D"));
        Map<String, Object> result = game.getResult();
        assertNotNull("result should not be null", result);
        assertFalse("won should be false initially", (Boolean) result.get("won"));
        assertEquals("moves in result should match moveCount", 1, ((Number) result.get("moves")).intValue());
        assertTrue("time should be non-negative", ((Number) result.get("time")).longValue() >= 0L);
    }

    @Test
    public void testReset_returnsPlayerToStart_andResetsMoveCount() {
        MazeGame game = new MazeGame();
        game.initialize(makePuzzleData());

        // make some moves to change state
        assertTrue(game.processInput("D"));
        assertTrue(game.processInput("D"));
        assertTrue(game.processInput("S"));
        assertTrue(game.processInput("S"));
        assertTrue(game.isGameOver());

        // reset
        game.reset();
        assertFalse("After reset game should not be over", game.isGameOver());

        Map<String, Object> state = game.getGameState();
        Player p = (Player) state.get("player");
        assertEquals("Player row should be reset to start row", 0, p.row);
        assertEquals("Player col should be reset to start col", 0, p.col);
        assertEquals("moveCount should be reset to 0", 0, ((Number) state.get("moveCount")).intValue());
    }

    @Test
    public void testSaveState_andRestoreState_roundTrip() {
        MazeGame game = new MazeGame();
        game.initialize(makePuzzleData());

        // move to (0,2) then to (1,2)
        assertTrue(game.processInput("D"));
        assertTrue(game.processInput("D"));
        assertTrue(game.processInput("S"));

        Map<String, Object> saved = game.saveState();

        // Create a new game and restore from saved
        MazeGame restored = new MazeGame();
        restored.restoreState(saved);

        Map<String, Object> restoredState = restored.getGameState();
        Maze restoredMaze = (Maze) restoredState.get("maze");
        Player restoredPlayer = (Player) restoredState.get("player");
        int restoredMoves = ((Number) restoredState.get("moveCount")).intValue();

        // Maze dimensions should match
        assertEquals("Restored maze width should match", 3, restoredMaze.getWidth());
        assertEquals("Restored maze height should match", 3, restoredMaze.getHeight());

        // Player position and moveCount should match saved values
        assertEquals("Restored player row should match saved", ((Number) ((Map) saved.get("player")).get("row")).intValue(), restoredPlayer.row);
        assertEquals("Restored player col should match saved", ((Number) ((Map) saved.get("player")).get("col")).intValue(), restoredPlayer.col);
        assertEquals("Restored moveCount should match saved", ((Number) saved.get("moveCount")).intValue(), restoredMoves);
    }
}
