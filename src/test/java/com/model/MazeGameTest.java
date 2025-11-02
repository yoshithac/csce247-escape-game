package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for MazeGame using JUnit 4.13
 * Tests maze navigation, movement, wall collision, state management, and save/restore
 */
public class MazeGameTest {
    
    private MazeGame game;
    private Map<String, Object> puzzleData;
    
    @Before
    public void setUp() {
        game = new MazeGame();
        puzzleData = createTestMazeData();
    }
    
    @After
    public void tearDown() {
        game = null;
        puzzleData = null;
    }
    
    /**
     * Helper method to create test maze data
     * Creates a 6x6 maze with walls around borders and internal walls:
     * █ █ █ █ █ █
     * █ S 0 0 0 █
     * █ 0 █ █ 0 █
     * █ 0 0 █ 0 █
     * █ █ 0 0 E █
     * █ █ █ █ █ █
     * Where S=start(1,1), E=end(4,4), 0=path, 1=wall, █=wall
     */
    private Map<String, Object> createTestMazeData() {
        Map<String, Object> data = new HashMap<>();
        data.put("width", 6);
        data.put("height", 6);
        
        // Create grid: 0=path, 1=wall
        List<List<Integer>> grid = new ArrayList<>();
        
        // Row 0: Top wall
        List<Integer> row0 = new ArrayList<>();
        row0.add(1); row0.add(1); row0.add(1); row0.add(1); row0.add(1); row0.add(1);
        
        // Row 1: █ S 0 0 0 █
        List<Integer> row1 = new ArrayList<>();
        row1.add(1); row1.add(0); row1.add(0); row1.add(0); row1.add(0); row1.add(1);
        
        // Row 2: █ 0 █ █ 0 █
        List<Integer> row2 = new ArrayList<>();
        row2.add(1); row2.add(0); row2.add(1); row2.add(1); row2.add(0); row2.add(1);
        
        // Row 3: █ 0 0 █ 0 █
        List<Integer> row3 = new ArrayList<>();
        row3.add(1); row3.add(0); row3.add(0); row3.add(1); row3.add(0); row3.add(1);
        
        // Row 4: █ █ 0 0 E █
        List<Integer> row4 = new ArrayList<>();
        row4.add(1); row4.add(1); row4.add(0); row4.add(0); row4.add(0); row4.add(1);
        
        // Row 5: Bottom wall
        List<Integer> row5 = new ArrayList<>();
        row5.add(1); row5.add(1); row5.add(1); row5.add(1); row5.add(1); row5.add(1);
        
        grid.add(row0);
        grid.add(row1);
        grid.add(row2);
        grid.add(row3);
        grid.add(row4);
        grid.add(row5);
        data.put("grid", grid);
        
        // Start position (1,1) - inside the walls
        Map<String, Object> start = new HashMap<>();
        start.put("row", 1);
        start.put("col", 1);
        data.put("start", start);
        
        // End position (4,4) - inside the walls
        Map<String, Object> end = new HashMap<>();
        end.put("row", 4);
        end.put("col", 4);
        data.put("end", end);
        
        return data;
    }
    
    // ===== INITIALIZATION TESTS =====
    
    @Test
    public void testInitializeCreatesMaze() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        
        assertNotNull(state.get("maze"));
    }
    
    @Test
    public void testInitializeCreatesPlayer() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        
        assertNotNull(state.get("player"));
    }
    
    @Test
    public void testInitializeSetsGameType() {
        game.initialize(puzzleData);
        
        assertEquals("MAZE", game.getGameType());
    }
    
    @Test
    public void testInitializeSetsZeroMoveCount() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        
        assertEquals(0, state.get("moveCount"));
    }
    
    @Test
    public void testInitializePlacesPlayerAtStart() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(1, player.row);
        assertEquals(1, player.col);
    }
    
    @Test
    public void testInitializeNotGameOver() {
        game.initialize(puzzleData);
        
        assertFalse(game.isGameOver());
    }
    
    // ===== MOVEMENT TESTS =====
    
    @Test
    public void testProcessInputWithValidMoveDown() {
        game.initialize(puzzleData);
        
        boolean result = game.processInput("S");
        
        assertTrue(result);
    }
    
    @Test
    public void testProcessInputWithValidMoveRight() {
        game.initialize(puzzleData);
        
        boolean result = game.processInput("D");
        
        assertTrue(result);
    }
    
    @Test
    public void testProcessInputMovesPlayerDown() {
        game.initialize(puzzleData);
        game.processInput("S");
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(2, player.row);
        assertEquals(1, player.col);
    }
    
    @Test
    public void testProcessInputMovesPlayerRight() {
        game.initialize(puzzleData);
        game.processInput("D");
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(1, player.row);
        assertEquals(2, player.col);
    }
    
    @Test
    public void testProcessInputWithLowercaseCommand() {
        game.initialize(puzzleData);
        
        boolean result = game.processInput("d");
        
        assertTrue(result);
    }
    
    @Test
    public void testProcessInputIncrementsMoveCount() {
        game.initialize(puzzleData);
        game.processInput("D");
        
        Map<String, Object> state = game.getGameState();
        
        assertEquals(1, state.get("moveCount"));
    }
    
    @Test
    public void testProcessInputWithMultipleMoves() {
        game.initialize(puzzleData);
        game.processInput("D");
        game.processInput("D");
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(1, player.row);
        assertEquals(3, player.col);
    }
    
    // ===== INVALID MOVEMENT TESTS =====
    
    @Test
    public void testProcessInputWithNullReturnsFalse() {
        game.initialize(puzzleData);
        
        boolean result = game.processInput(null);
        
        assertFalse(result);
    }
    
    @Test
    public void testProcessInputWithInvalidCommandReturnsFalse() {
        game.initialize(puzzleData);
        
        boolean result = game.processInput("X");
        
        assertFalse(result);
    }
    
    @Test
    public void testProcessInputOutOfBoundsUpReturnsFalse() {
        game.initialize(puzzleData);
        
        boolean result = game.processInput("W"); // Try to move up into wall
        
        assertFalse(result);
    }
    
    @Test
    public void testProcessInputOutOfBoundsLeftReturnsFalse() {
        game.initialize(puzzleData);
        
        boolean result = game.processInput("A"); // Try to move left into wall
        
        assertFalse(result);
    }
    
    @Test
    public void testProcessInputDoesNotChangeMoveCountOnInvalidMove() {
        game.initialize(puzzleData);
        game.processInput("W"); // Invalid move
        
        Map<String, Object> state = game.getGameState();
        
        assertEquals(0, state.get("moveCount"));
    }
    
    @Test
    public void testProcessInputDoesNotMovePlayerOnInvalidMove() {
        game.initialize(puzzleData);
        game.processInput("W"); // Invalid move
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(1, player.row);
        assertEquals(1, player.col);
    }
    
    // ===== WALL COLLISION TESTS =====
    
    @Test
    public void testProcessInputIntoWallReturnsFalse() {
        game.initialize(puzzleData);
        game.processInput("S"); // Move to (2,1)
        
        boolean result = game.processInput("D"); // Try to move right into wall at (2,2)
        
        assertFalse(result);
    }
    
    @Test
    public void testProcessInputIntoWallDoesNotMovePlayer() {
        game.initialize(puzzleData);
        game.processInput("S"); // Move to (2,1)
        game.processInput("D"); // Try to move into wall
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(2, player.row);
        assertEquals(1, player.col); // Should still be at (2,1)
    }
    
    @Test
    public void testProcessInputIntoWallDoesNotIncrementMoveCount() {
        game.initialize(puzzleData);
        game.processInput("S"); // Move 1: (1,1) -> (2,1)
        game.processInput("D"); // Invalid: try to move into wall
        
        Map<String, Object> state = game.getGameState();
        
        assertEquals(1, state.get("moveCount")); // Should still be 1, not 2
    }
    
    @Test
    public void testMultipleWallCollisionsDoNotAffectState() {
        game.initialize(puzzleData);
        game.processInput("S"); // Move to (2,1)
        
        game.processInput("D"); // Try wall at (2,2)
        game.processInput("D"); // Try wall again
        game.processInput("D"); // Try wall again
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(2, player.row);
        assertEquals(1, player.col);
        assertEquals(1, state.get("moveCount")); // Only 1 successful move
    }
    
    @Test
    public void testCanNavigateAroundWalls() {
        game.initialize(puzzleData);
        
        // Navigate around walls: (1,1) -> (1,2) -> (1,3)
        game.processInput("D"); // (1,2)
        game.processInput("D"); // (1,3)
        
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(1, player.row);
        assertEquals(3, player.col);
        assertEquals(2, state.get("moveCount"));
    }
    
    // ===== GAME OVER TESTS =====
    
    @Test
    public void testIsGameOverReturnsFalseInitially() {
        game.initialize(puzzleData);
        
        assertFalse(game.isGameOver());
    }
    
    @Test
    public void testIsGameOverReturnsTrueWhenAtEnd() {
        game.initialize(puzzleData);
        
        // Navigate to end position (4,4) avoiding walls
        // Path: (1,1) -> (1,2) -> (1,3) -> (1,4) -> (2,4) -> (3,4) -> (4,4)
        game.processInput("D"); // (1,2)
        game.processInput("D"); // (1,3)
        game.processInput("D"); // (1,4)
        game.processInput("S"); // (2,4)
        game.processInput("S"); // (3,4)
        game.processInput("S"); // (4,4) - End position
        
        assertTrue(game.isGameOver());
    }
    
    @Test
    public void testIsGameOverReturnsFalseNearEnd() {
        game.initialize(puzzleData);
        
        // Navigate close to end but not at it
        game.processInput("D");
        game.processInput("D");
        game.processInput("D");
        game.processInput("S");
        game.processInput("S");
        
        assertFalse(game.isGameOver());
    }
    
    // ===== GET GAME STATE TESTS =====
    
    @Test
    public void testGetGameStateReturnsNonNull() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        
        assertNotNull(state);
    }
    
    @Test
    public void testGetGameStateContainsMaze() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        
        assertTrue(state.containsKey("maze"));
    }
    
    @Test
    public void testGetGameStateContainsPlayer() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        
        assertTrue(state.containsKey("player"));
    }
    
    @Test
    public void testGetGameStateContainsMoveCount() {
        game.initialize(puzzleData);
        
        Map<String, Object> state = game.getGameState();
        
        assertTrue(state.containsKey("moveCount"));
    }
    
    // ===== GET RESULT TESTS =====
    
    @Test
    public void testGetResultReturnsNonNull() {
        game.initialize(puzzleData);
        
        Map<String, Object> result = game.getResult();
        
        assertNotNull(result);
    }
    
    @Test
    public void testGetResultContainsWonStatus() {
        game.initialize(puzzleData);
        
        Map<String, Object> result = game.getResult();
        
        assertTrue(result.containsKey("won"));
    }
    
    @Test
    public void testGetResultContainsTime() {
        game.initialize(puzzleData);
        
        Map<String, Object> result = game.getResult();
        
        assertTrue(result.containsKey("time"));
    }
    
    @Test
    public void testGetResultContainsMoves() {
        game.initialize(puzzleData);
        
        Map<String, Object> result = game.getResult();
        
        assertTrue(result.containsKey("moves"));
    }
    
    @Test
    public void testGetResultShowsNotWonInitially() {
        game.initialize(puzzleData);
        
        Map<String, Object> result = game.getResult();
        
        assertFalse((Boolean) result.get("won"));
    }
    
    @Test
    public void testGetResultShowsWonAfterReachingEnd() {
        game.initialize(puzzleData);
        
        // Navigate to end
        game.processInput("D");
        game.processInput("D");
        game.processInput("D");
        game.processInput("S");
        game.processInput("S");
        game.processInput("S");
        
        Map<String, Object> result = game.getResult();
        
        assertTrue((Boolean) result.get("won"));
    }
    
    @Test
    public void testGetResultShowsCorrectMoveCount() {
        game.initialize(puzzleData);
        game.processInput("D");
        game.processInput("D");
        
        Map<String, Object> result = game.getResult();
        
        assertEquals(2, result.get("moves"));
    }
    
    // ===== RESET TESTS =====
    
    @Test
    public void testResetClearsMoveCount() {
        game.initialize(puzzleData);
        game.processInput("D");
        game.processInput("D");
        
        game.reset();
        Map<String, Object> state = game.getGameState();
        
        assertEquals(0, state.get("moveCount"));
    }
    
    @Test
    public void testResetMovesPlayerToStart() {
        game.initialize(puzzleData);
        game.processInput("D");
        game.processInput("S");
        
        game.reset();
        Map<String, Object> state = game.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(1, player.row);
        assertEquals(1, player.col);
    }
    
    @Test
    public void testResetSetsNotGameOver() {
        game.initialize(puzzleData);
        
        // Navigate to end
        game.processInput("D");
        game.processInput("D");
        game.processInput("D");
        game.processInput("S");
        game.processInput("S");
        game.processInput("S");
        
        game.reset();
        
        assertFalse(game.isGameOver());
    }
    
    // ===== SAVE STATE TESTS =====
    
    @Test
    public void testSaveStateReturnsNonNull() {
        game.initialize(puzzleData);
        
        Map<String, Object> savedState = game.saveState();
        
        assertNotNull(savedState);
    }
    
    @Test
    public void testSaveStateContainsMaze() {
        game.initialize(puzzleData);
        
        Map<String, Object> savedState = game.saveState();
        
        assertTrue(savedState.containsKey("maze"));
    }
    
    @Test
    public void testSaveStateContainsPlayer() {
        game.initialize(puzzleData);
        
        Map<String, Object> savedState = game.saveState();
        
        assertTrue(savedState.containsKey("player"));
    }
    
    @Test
    public void testSaveStateContainsMoveCount() {
        game.initialize(puzzleData);
        
        Map<String, Object> savedState = game.saveState();
        
        assertTrue(savedState.containsKey("moveCount"));
    }
    
    @Test
    public void testSaveStateContainsStartTime() {
        game.initialize(puzzleData);
        
        Map<String, Object> savedState = game.saveState();
        
        assertTrue(savedState.containsKey("startTime"));
    }
    
    @Test
    public void testSaveStatePreservesMoveCount() {
        game.initialize(puzzleData);
        game.processInput("D");
        game.processInput("D");
        
        Map<String, Object> savedState = game.saveState();
        
        assertEquals(2, ((Number) savedState.get("moveCount")).intValue());
    }
    
    @Test
    public void testSaveStatePreservesPlayerPosition() {
        game.initialize(puzzleData);
        game.processInput("S"); // (1,1) -> (2,1) - valid move down
        game.processInput("D"); // (2,1) -> (2,2) is WALL, stays at (2,1)
        
        Map<String, Object> savedState = game.saveState();
        @SuppressWarnings("unchecked")
        Map<String, Object> playerData = (Map<String, Object>) savedState.get("player");
        
        assertEquals(2, ((Number) playerData.get("row")).intValue()); // At row 2
        assertEquals(1, ((Number) playerData.get("col")).intValue()); // At col 1
    }
    
    // ===== RESTORE STATE TESTS =====
    
    @Test
    public void testRestoreStateRestoresMoveCount() {
        game.initialize(puzzleData);
        game.processInput("D");
        game.processInput("D");
        
        Map<String, Object> savedState = game.saveState();
        
        MazeGame newGame = new MazeGame();
        newGame.restoreState(savedState);
        
        Map<String, Object> state = newGame.getGameState();
        assertEquals(2, state.get("moveCount"));
    }
    
    @Test
    public void testRestoreStateRestoresPlayerPosition() {
        game.initialize(puzzleData);
        game.processInput("S"); // (1,1) -> (2,1) - valid move down
        game.processInput("D"); // (2,1) -> (2,2) is WALL, stays at (2,1)
        
        Map<String, Object> savedState = game.saveState();
        
        MazeGame newGame = new MazeGame();
        newGame.restoreState(savedState);
        
        Map<String, Object> state = newGame.getGameState();
        Player player = (Player) state.get("player");
        
        assertEquals(2, player.row); // At row 2
        assertEquals(1, player.col); // At col 1
    }
    
    @Test
    public void testRestoreStatePreservesGameType() {
        game.initialize(puzzleData);
        Map<String, Object> savedState = game.saveState();
        
        MazeGame newGame = new MazeGame();
        newGame.restoreState(savedState);
        
        assertEquals("MAZE", newGame.getGameType());
    }
    
    @Test
    public void testRestoreStateAllowsContinuingGame() {
        game.initialize(puzzleData);
        game.processInput("D");
        
        Map<String, Object> savedState = game.saveState();
        
        MazeGame newGame = new MazeGame();
        newGame.restoreState(savedState);
        
        // Should be able to continue moving
        boolean result = newGame.processInput("D");
        assertTrue(result);
    }
    
    @Test
    public void testRestoreStatePreservesGameOverStatus() {
        game.initialize(puzzleData);
        
        // Navigate to end
        game.processInput("D");
        game.processInput("D");
        game.processInput("D");
        game.processInput("S");
        game.processInput("S");
        game.processInput("S");
        
        Map<String, Object> savedState = game.saveState();
        
        MazeGame newGame = new MazeGame();
        newGame.restoreState(savedState);
        
        assertTrue(newGame.isGameOver());
    }
    
    // ===== GET GAME TYPE TEST =====
    
    @Test
    public void testGetGameTypeReturnsMaze() {
        game.initialize(puzzleData);
        
        assertEquals("MAZE", game.getGameType());
    }
}
