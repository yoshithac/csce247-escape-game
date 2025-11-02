package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * JUnit4 tests for MatchingGame (tests all non-getter/setter behavior)
 */
public class MatchingGameTest {

    /**
     * Helper to build a small puzzleData map
     */
    private Map<String, Object> makePuzzleData(int height, int width, String... values) {
        Map<String, Object> data = new HashMap<>();
        data.put("height", height);
        data.put("width", width);

        List<Map<String, Object>> cardsList = new ArrayList<>();
        for (String v : values) {
            Map<String, Object> m = new HashMap<>();
            m.put("value", v);
            cardsList.add(m);
        }
        data.put("cards", cardsList);
        return data;
    }

    /**
     * Helper to extract the board and matched arrays from getGameState()
     */
    private String[][] extractBoard(Map<String, Object> state) {
        return (String[][]) state.get("board");
    }

    private boolean[][] extractMatched(Map<String, Object> state) {
        return (boolean[][]) state.get("matched");
    }

    /**
     * Helper to find two coordinates for a value on the board.
     * Returns array of [r1,c1,r2,c2]
     */
    private int[] findPair(String[][] board, String value) {
        List<int[]> coords = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (value.equals(board[r][c])) {
                    coords.add(new int[]{r, c});
                }
            }
        }
        if (coords.size() < 2) {
            throw new IllegalStateException("Pair not found for value: " + value);
        }
        int[] a = coords.get(0);
        int[] b = coords.get(1);
        return new int[]{a[0], a[1], b[0], b[1]};
    }

    @Test
    public void testInitialize_andGetGameType_andGetGameStateStructure() {
        MatchingGame game = new MatchingGame();
        Map<String, Object> puzzle = makePuzzleData(2, 2, "A", "B"); // will create pairs A,A,B,B
        game.initialize(puzzle);

        assertEquals("MATCHING", game.getGameType());

        Map<String, Object> state = game.getGameState();
        assertNotNull("getGameState should not be null", state);

        String[][] board = extractBoard(state);
        assertEquals(2, board.length);
        assertEquals(2, board[0].length);

        boolean[][] matched = extractMatched(state);
        assertEquals(2, matched.length);
        assertFalse("No cards should be matched initially", matched[0][0] || matched[0][1] || matched[1][0] || matched[1][1]);
    }

    @Test
    public void testProcessInput_invalidFormat_and_nonNumeric_and_outOfBounds() {
        MatchingGame game = new MatchingGame();
        Map<String, Object> puzzle = makePuzzleData(2, 2, "A", "B");
        game.initialize(puzzle);
        
        // invalid format (too few parts)
        assertFalse("Should return false for invalid format", game.processInput("0"));

        // non-numeric
        assertFalse("Should return false for non-numeric input", game.processInput("x y"));

        // out of bounds negative
        assertFalse("Should return false for out-of-bounds coords", game.processInput("-1 0"));
        
        // out of bounds too large
        assertFalse("Should return false for out-of-bounds coords", game.processInput("10 10"));
    }

    @Test
    public void testSelectingSameCardTwice_returnsFalse() {
        MatchingGame game = new MatchingGame();
        Map<String, Object> puzzle = makePuzzleData(2, 2, "A", "B");
        game.initialize(puzzle);

        Map<String, Object> state = game.getGameState();
        String[][] board = extractBoard(state);

        // pick any coordinate for first pick
        int r = 0, c = 0;
        assertTrue(game.processInput(r + " " + c));
        // attempt to pick the same again should be false
        assertFalse(game.processInput(r + " " + c));
    }

    @Test
    public void testMatchingPair_setsMatched_andIncrementsMoves_andIsShowingPair() {
        MatchingGame game = new MatchingGame();
        Map<String, Object> puzzle = makePuzzleData(2, 2, "A", "B");
        game.initialize(puzzle);

        Map<String, Object> state = game.getGameState();
        String[][] board = extractBoard(state);

        // find coordinates of a pair (value A or B)
        int[] coords = findPair(board, board[0][0]); // value at 0,0
        int r1 = coords[0], c1 = coords[1], r2 = coords[2], c2 = coords[3];

        // first pick
        assertTrue(game.processInput(r1 + " " + c1));
        // second pick (different coord)
        assertTrue(game.processInput(r2 + " " + c2));

        // After picking a pair, moveCount should be incremented and showingPair true
        Map<String, Object> after = game.getGameState();
        assertTrue("should be showingPair after selecting two cards", game.isShowingPair());
        assertEquals("moveCount should be 1 after matching attempt", 1, ((Number) after.get("moveCount")).intValue());

        // matched array should mark both positions true if they match
        boolean[][] matched = extractMatched(after);
        if (board[r1][c1].equals(board[r2][c2])) {
            assertTrue("first should be matched", matched[r1][c1]);
            assertTrue("second should be matched", matched[r2][c2]);
        }

        // clear selection should reset showingPair and first/second
        game.clearSelection();
        assertFalse("showingPair should be false after clearSelection", game.isShowingPair());
        Map<String, Object> cleared = game.getGameState();
        assertNull("firstCard should be null after clearSelection", cleared.get("firstCard"));
        assertNull("secondCard should be null after clearSelection", cleared.get("secondCard"));
    }

    @Test
    public void testSelectingAlreadyMatchedCard_returnsFalse() {
        MatchingGame game = new MatchingGame();
        Map<String, Object> puzzle = makePuzzleData(2, 2, "A", "B");
        game.initialize(puzzle);

        Map<String, Object> state = game.getGameState();
        String[][] board = extractBoard(state);
        int[] coords = findPair(board, board[0][0]);
        int r1 = coords[0], c1 = coords[1], r2 = coords[2], c2 = coords[3];

        // match them
        assertTrue(game.processInput(r1 + " " + c1));
        assertTrue(game.processInput(r2 + " " + c2));
        game.clearSelection();

        // trying to select an already matched card should return false
        assertFalse("Selecting an already matched card should return false", game.processInput(r1 + " " + c1));
    }

    @Test
    public void testIsGameOver_andReset_andGetResult_movesAndWonFlag() {
        MatchingGame game = new MatchingGame();
        Map<String, Object> puzzle = makePuzzleData(2, 2, "A", "B");
        game.initialize(puzzle);

        // match all pairs by discovering pairs and selecting them
        Map<String, Object> state = game.getGameState();
        String[][] board = extractBoard(state);

        // collect distinct values
        Set<String> values = new HashSet<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                values.add(board[r][c]);
            }
        }

        int totalMoves = 0;
        for (String val : values) {
            int[] coords = findPair(board, val);
            assertTrue(game.processInput(coords[0] + " " + coords[1]));
            assertTrue(game.processInput(coords[2] + " " + coords[3]));
            totalMoves++;
            game.clearSelection();
        }

        assertTrue("Game should be over after matching all pairs", game.isGameOver());

        Map<String, Object> result = game.getResult();
        assertTrue("result should contain 'won' true when game over", (Boolean) result.get("won"));
        assertEquals("moves in result should match moveCount", totalMoves, ((Number) result.get("moves")).intValue());
        assertTrue("time should be non-negative", ((Number) result.get("time")).longValue() >= 0L);

        // reset should make game not over and moveCount zero
        game.reset();
        assertFalse("After reset game should not be over", game.isGameOver());
        Map<String, Object> afterReset = game.getGameState();
        assertEquals(0, ((Number) afterReset.get("moveCount")).intValue());
    }

    @Test
    public void testSaveState_andRestoreState_roundTrip() {
        MatchingGame game = new MatchingGame();
        Map<String, Object> puzzle = makePuzzleData(2, 2, "A", "B");
        game.initialize(puzzle);

        // Do one matching attempt (may or may not match depending on shuffle)
        Map<String, Object> state = game.getGameState();
        String[][] board = extractBoard(state);
        int[] coords = findPair(board, board[0][0]);

        // pick first and second (not asserting match)
        assertTrue(game.processInput(coords[0] + " " + coords[1]));
        assertTrue(game.processInput(coords[2] + " " + coords[3]));
        game.clearSelection();

        // Build a savedState map in the format restoreState expects
        Map<String, Object> saved = new HashMap<>();
        saved.put("rows", 2);
        saved.put("cols", 2);
        saved.put("moveCount", ((Number) game.getGameState().get("moveCount")).intValue());
        saved.put("startTime", System.currentTimeMillis());

        // firstCard and secondCard saved as Maps with row/col (or null)
        saved.put("firstCard", null);
        saved.put("secondCard", null);

        // board as List<List<String>>
        List<List<String>> boardList = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            List<String> rowList = new ArrayList<>();
            for (int c = 0; c < board[0].length; c++) {
                rowList.add(board[r][c]);
            }
            boardList.add(rowList);
        }
        saved.put("board", boardList);

        // matched as List<List<Boolean>>
        boolean[][] matchedArr = extractMatched(game.getGameState());
        List<List<Boolean>> matchedList = new ArrayList<>();
        for (int r = 0; r < matchedArr.length; r++) {
            List<Boolean> rowList = new ArrayList<>();
            for (int c = 0; c < matchedArr[0].length; c++) {
                rowList.add(matchedArr[r][c]);
            }
            matchedList.add(rowList);
        }
        saved.put("matched", matchedList);

        // Create a fresh game and restore
        MatchingGame restored = new MatchingGame();
        restored.restoreState(saved);

        Map<String, Object> restoredState = restored.getGameState();
        String[][] restoredBoard = extractBoard(restoredState);
        boolean[][] restoredMatched = extractMatched(restoredState);

        // boards should match values
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                assertEquals("Board values should be restored", board[r][c], restoredBoard[r][c]);
            }
        }

        // matched arrays should match
        for (int r = 0; r < matchedArr.length; r++) {
            for (int c = 0; c < matchedArr[0].length; c++) {
                assertEquals("Matched values should be restored", matchedArr[r][c], restoredMatched[r][c]);
            }
        }

        assertEquals("moveCount should be restored", ((Number) saved.get("moveCount")).intValue(),
                ((Number) restoredState.get("moveCount")).intValue());
    }
}
