package com.model;

import java.util.*;

/**
 * Matching card game implementation
 * Players flip cards to find matching pairs
 * Includes save/restore state for resume functionality
 */
public class MatchingGame implements PuzzleGame {
    private String[][] board;
    private boolean[][] matched;
    private Position firstCard;
    private Position secondCard;
    private int moveCount;
    private long startTime;
    private int rows;
    private int cols;
    private boolean showingPair;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Map<String, Object> puzzleData) {
        // Get dimensions from JSON like maze does
        this.rows = ((Number) puzzleData.get("height")).intValue();
        this.cols = ((Number) puzzleData.get("width")).intValue();
        
        this.board = new String[rows][cols];
        this.matched = new boolean[rows][cols];
        this.firstCard = null;
        this.secondCard = null;
        this.moveCount = 0;
        this.startTime = System.currentTimeMillis();
        this.showingPair = false;

        // Get cards from puzzle data
        List<Map<String, Object>> cardsList = (List<Map<String, Object>>) puzzleData.get("cards");
        List<String> cards = new ArrayList<>();
        for (Map<String, Object> card : cardsList) {
            String value = (String) card.get("value");
            cards.add(value);
            cards.add(value); // Add duplicate for matching
        }

        // Shuffle and place on board
        Collections.shuffle(cards);
        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = cards.get(index++);
                matched[r][c] = false;
            }
        }
    }

    @Override
    public boolean processInput(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            return false;
        }

        try {
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            if (row < 0 || row >= rows || col < 0 || col >= cols) {
                return false;
            }

            if (matched[row][col]) {
                return false; // Already matched
            }

            if (firstCard == null) {
                firstCard = new Position(row, col);
                return true;
            } else if (secondCard == null) {
                // Don't allow selecting same card twice
                if (firstCard.getRow() == row && firstCard.getCol() == col) {
                    return false;
                }

                secondCard = new Position(row, col);
                moveCount++;
                showingPair = true;

                // Check for match
                String card1 = board[firstCard.getRow()][firstCard.getCol()];
                String card2 = board[secondCard.getRow()][secondCard.getCol()];
                if (card1.equals(card2)) {
                    matched[firstCard.getRow()][firstCard.getCol()] = true;
                    matched[secondCard.getRow()][secondCard.getCol()] = true;
                }

                return true;
            }

            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if we're currently showing a pair of cards
     */
    public boolean isShowingPair() {
        return showingPair;
    }

    /**
     * Clear current selection (called after showing matched/mismatched cards)
     */
    public void clearSelection() {
        firstCard = null;
        secondCard = null;
        showingPair = false;
    }

    @Override
    public boolean isGameOver() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!matched[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("board", board);
        state.put("matched", matched);
        state.put("firstCard", firstCard);
        state.put("secondCard", secondCard);
        state.put("moveCount", moveCount);
        return state;
    }

    @Override
    public String getGameType() {
        return "MATCHING";
    }

    @Override
    public Map<String, Object> getResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("won", isGameOver());
        result.put("time", System.currentTimeMillis() - startTime);
        result.put("moves", moveCount);
        return result;
    }

    @Override
    public void reset() {
        matched = new boolean[rows][cols];
        firstCard = null;
        secondCard = null;
        moveCount = 0;
        showingPair = false;
        startTime = System.currentTimeMillis();
    }

    /*
    @Override
    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("rows", rows);
        state.put("cols", cols);
        state.put("board", board);
        state.put("matched", matched);
        state.put("moveCount", moveCount);
        state.put("startTime", startTime);
        state.put("firstCard", firstCard);
        state.put("secondCard", secondCard);
        return state;
    }
*/
    @Override
    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("rows", rows);
        state.put("cols", cols);
        state.put("moveCount", moveCount);
        state.put("startTime", startTime);
        state.put("firstCard", firstCard);
        state.put("secondCard", secondCard);
        
        // Convert board array to List for JSON compatibility
        List<List<String>> boardList = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<String> rowList = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                rowList.add(board[r][c]);
            }
            boardList.add(rowList);
        }
        state.put("board", boardList);
        
        // Convert matched array to List for JSON compatibility
        List<List<Boolean>> matchedList = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<Boolean> rowList = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                rowList.add(matched[r][c]);
            }
            matchedList.add(rowList);
        }
        state.put("matched", matchedList);
        
        return state;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public void restoreState(Map<String, Object> savedState) {
        this.rows = ((Number) savedState.get("rows")).intValue();
        this.cols = ((Number) savedState.get("cols")).intValue();
        this.moveCount = ((Number) savedState.get("moveCount")).intValue();
        this.startTime = ((Number) savedState.get("startTime")).longValue();
        this.firstCard = savedState.get("firstCard") != null ? 
            new Position(
                ((Number)((Map<String, Object>)savedState.get("firstCard")).get("row")).intValue(),
                ((Number)((Map<String, Object>)savedState.get("firstCard")).get("col")).intValue()
            ) : null;
        this.secondCard = savedState.get("secondCard") != null ?
            new Position(
                ((Number)((Map<String, Object>)savedState.get("secondCard")).get("row")).intValue(),
                ((Number)((Map<String, Object>)savedState.get("secondCard")).get("col")).intValue()
            ) : null;
        this.showingPair = false;

        // Handle board restoration from JSON (ArrayList of ArrayLists)
        List<List<String>> boardList = (List<List<String>>) savedState.get("board");
        this.board = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.board[r][c] = boardList.get(r).get(c);
            }
        }

        // Handle matched restoration from JSON (ArrayList of ArrayLists of Booleans)
        List<List<Boolean>> matchedList = (List<List<Boolean>>) savedState.get("matched");
        this.matched = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.matched[r][c] = matchedList.get(r).get(c);
            }
        }
    }
}
