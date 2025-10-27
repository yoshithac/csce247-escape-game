<<<<<<< HEAD
package com.model;

import java.util.*;

/**
 * Maze game implementation with save/resume functionality
 * Player navigates through maze from start to end position
 */
public class MazeGame implements PuzzleGame {
    private Maze maze;
    private Player player;
    private int moveCount;
    private long startTime;
    
    @Override
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
    
    @Override
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
    
    @Override
    public boolean isGameOver() {
        return player.row == maze.getEnd().getRow() && 
               player.col == maze.getEnd().getCol();
    }
    
    @Override
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("maze", maze);
        state.put("player", player);
        state.put("moveCount", moveCount);
        return state;
    }
    
    @Override
    public String getGameType() {
        return "MAZE";
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
        player.row = maze.getStart().getRow();
        player.col = maze.getStart().getCol();
        moveCount = 0;
        startTime = System.currentTimeMillis();
    }
    
    @Override
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
    
    @Override
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
=======
package com.model;

import java.util.*;

/**
 * Maze game implementation with save/resume functionality
 * Player navigates through maze from start to end position
 */
public class MazeGame implements PuzzleGame {
    private Maze maze;
    private Player player;
    private int moveCount;
    private long startTime;
    
    @Override
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
    
    @Override
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
    
    @Override
    public boolean isGameOver() {
        return player.row == maze.getEnd().getRow() && 
               player.col == maze.getEnd().getCol();
    }
    
    @Override
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("maze", maze);
        state.put("player", player);
        state.put("moveCount", moveCount);
        return state;
    }
    
    @Override
    public String getGameType() {
        return "MAZE";
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
        player.row = maze.getStart().getRow();
        player.col = maze.getStart().getCol();
        moveCount = 0;
        startTime = System.currentTimeMillis();
    }
    
    @Override
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
    
    @Override
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
>>>>>>> main
