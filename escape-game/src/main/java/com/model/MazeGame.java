package com.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maze game implementation with save/resume functionality
 * Player navigates through maze from start to end position
 */
public class MazeGame implements PuzzleGame {
    private Maze maze;
    private Player player;
    private int moveCount;
    private long startTime;
    
    /**
     * Initialize maze game with puzzle data
     * @param puzzleData Map of puzzle data
     * @Override
     * @SuppressWarnings("unchecked")
     */
    public void initialize(Map<String, Object> puzzleData) {
        // Extract maze data
        int width = ((Number) puzzleData.get("width")).intValue();
        int height = ((Number) puzzleData.get("height")).intValue();
        
        // Convert grid data
        List<List<Number>> gridList = (List<List<Number>>) puzzleData.get("grid");
        int[][] grid = new int[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = gridList.get(r).get(c).intValue();
            }
        }
        
        // Extract positions
        Map<String, Object> startData = (Map<String, Object>) puzzleData.get("start");
        Position start = new Position(
            ((Number) startData.get("row")).intValue(),
            ((Number) startData.get("col")).intValue()
        );
        
        Map<String, Object> endData = (Map<String, Object>) puzzleData.get("end");
        Position end = new Position(
            ((Number) endData.get("row")).intValue(),
            ((Number) endData.get("col")).intValue()
        );
        
        this.maze = new Maze(width, height, grid, start, end);
        this.player = new Player(start.getRow(), start.getCol());
        this.moveCount = 0;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Process player input for movement
     * @param input Player input command (WASD)
     * @return boolean indicating if move was successful
     * @Override
     */
    public boolean processInput(String input) {
        input = input.toUpperCase().trim();
        
        int newRow = player.row;
        int newCol = player.col;
        
        switch (input) {
            case "W": newRow--; break;  // Up
            case "S": newRow++; break;  // Down
            case "A": newCol--; break;  // Left
            case "D": newCol++; break;  // Right
            default: return false;
        }
        
        // Check bounds
        if (newRow < 0 || newRow >= maze.getHeight() || 
            newCol < 0 || newCol >= maze.getWidth()) {
            return false;
        }
        
        // Check walls
        if (maze.getMazeData()[newRow][newCol] == 1) {
            return false;
        }
        
        // Valid move
        player.row = newRow;
        player.col = newCol;
        moveCount++;
        return true;
    }
    
    /**
     * Check if game is over (player reached end)
     * @return boolean indicating if game is over
     */
    public boolean isGameOver() {
        return player.row == maze.getEnd().getRow() && 
               player.col == maze.getEnd().getCol();
    }
    
    /**
     * Get current game state for display
     * @return Map<String, Object> representing game state
     */
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("maze", maze);
        state.put("player", player);
        state.put("moveCount", moveCount);
        return state;
    }
    
    /**
     * Get game type
     * @return String game type
     * @Override
     */
    public String getGameType() {
        return "MAZE";
    }
    
    /**
     * Get result summary
     * @return Map<String, Object> result data
     * @Override
     */
    public Map<String, Object> getResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("won", isGameOver());
        result.put("time", System.currentTimeMillis() - startTime);
        result.put("moves", moveCount);
        return result;
    }
    
    /**
     * Reset game to initial state
     * @Override
     */
    public void reset() {
        player.row = maze.getStart().getRow();
        player.col = maze.getStart().getCol();
        moveCount = 0;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Save game state for resume functionality
     * @return Map<String, Object> saved state
     * @Override
     */
    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        
        // Save maze structure
        Map<String, Object> mazeData = new HashMap<>();
        mazeData.put("width", maze.getWidth());
        mazeData.put("height", maze.getHeight());
        mazeData.put("grid", maze.getMazeData());
        mazeData.put("start", maze.getStart());
        mazeData.put("end", maze.getEnd());
        state.put("maze", mazeData);
        
        // Save player position
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("row", player.row);
        playerData.put("col", player.col);
        state.put("player", playerData);
        
        // Save game progress
        state.put("moveCount", moveCount);
        state.put("startTime", startTime);
        
        return state;
    }
    
    /**
     * Restore game state from saved data
     * @param savedState Map<String, Object> saved state
     * @Override
     */
    public void restoreState(Map<String, Object> savedState) {
        // Restore maze
        Map<String, Object> mazeData = (Map<String, Object>) savedState.get("maze");
        int width = ((Number) mazeData.get("width")).intValue();
        int height = ((Number) mazeData.get("height")).intValue();
        int[][] grid = (int[][]) mazeData.get("grid");
        Position start = (Position) mazeData.get("start");
        Position end = (Position) mazeData.get("end");
        this.maze = new Maze(width, height, grid, start, end);
        
        // Restore player
        Map<String, Object> playerData = (Map<String, Object>) savedState.get("player");
        int row = ((Number) playerData.get("row")).intValue();
        int col = ((Number) playerData.get("col")).intValue();
        this.player = new Player(row, col);
        
        // Restore progress
        this.moveCount = ((Number) savedState.get("moveCount")).intValue();
        this.startTime = ((Number) savedState.get("startTime")).longValue();
    }
}
