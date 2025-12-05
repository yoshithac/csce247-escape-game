package com.escapegame.controller;

import com.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.Map;

/**
 * MazeGameController - UI Controller for Maze Puzzle View
 * 
 * Extends BasePuzzleController for common functionality:
 * - Timer management
 * - Background setup
 * - Save/back navigation
 * - Game completion handling
 * 
 * This controller handles:
 * - Maze rendering
 * - Keyboard input processing
 * - Move tracking
 */
public class MazeGameController extends BasePuzzleController {

    // Additional FXML components specific to Maze
    @FXML private Label statsLabel;
    @FXML private GridPane mazeGrid;

    // Game model
    private MazeGame game;

    // ==================== ABSTRACT METHOD IMPLEMENTATIONS ====================

    @Override
    protected void initializeGame() {
        game = new MazeGame();
        
        // Restore or initialize
        restoreOrInitialize(
            () -> game.restoreState(getSavedGameState()),
            () -> game.initialize(puzzle.getData())
        );
        
        // Setup keyboard controls after scene is ready
        mazeGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
            }
        });
    }

    @Override
    protected void renderGame() {
        Map<String, Object> state = game.getGameState();
        Maze maze = (Maze) state.get("maze");
        Player player = (Player) state.get("player");
        int moves = (int) state.get("moveCount");
        
        // Update stats
        if (statsLabel != null) {
            statsLabel.setText("Moves: " + moves);
        }
        
        // Render maze grid (skip outer walls - render only inner cells)
        mazeGrid.getChildren().clear();
        if (!mazeGrid.getStyleClass().contains("maze-grid-container")) {
            mazeGrid.getStyleClass().add("maze-grid-container");
        }
        
        int[][] mazeData = maze.getMazeData();
        Position end = maze.getEnd();
        
        int height = maze.getHeight();
        int width = maze.getWidth();
        
        // Inner grid dimensions (excluding outer walls)
        int innerRows = height - 2;
        int innerCols = width - 2;
        
        // Calculate dynamic cell size
        int cellSize = calculateCellSize(innerRows, innerCols);
        
        // Render only inner cells (skip row 0, row height-1, col 0, col width-1)
        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("maze-cell");
                
                // Apply dynamic cell size
                cell.setPrefSize(cellSize, cellSize);
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                
                if (player.row == r && player.col == c) {
                    cell.getStyleClass().add("maze-player");
                } else if (end.getRow() == r && end.getCol() == c) {
                    cell.getStyleClass().add("maze-exit");
                } else if (mazeData[r][c] == 1) {
                    cell.getStyleClass().add("maze-wall");
                } else {
                    cell.getStyleClass().add("maze-path");
                }
                
                // Adjust grid position (c-1, r-1) since we skip outer walls
                mazeGrid.add(cell, c - 1, r - 1);
            }
        }
        
        // Constrain grid size to fit content exactly (border hugs the maze)
        mazeGrid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }
    
    /**
     * Calculate optimal cell size based on available space and maze dimensions
     * @param innerRows number of rows in inner grid (excluding outer walls)
     * @param innerCols number of columns in inner grid (excluding outer walls)
     * @return cell size in pixels (between MIN_CELL_SIZE and MAX_CELL_SIZE)
     */
    private int calculateCellSize(int innerRows, int innerCols) {
        final int MIN_CELL_SIZE = 15;
        final int MAX_CELL_SIZE = 30;
        
        // Get available space from rootPane
        double availableWidth = rootPane.getWidth();
        double availableHeight = rootPane.getHeight();
        
        // Fallback if dimensions not yet available
        if (availableWidth <= 0 || availableHeight <= 0) {
            return MAX_CELL_SIZE;
        }
        
        // Account for frame padding, header, footer, and margins
        // Header ~70px, Footer ~60px, VBox padding 40px, border 12px, extra margin 40px
        double contentWidth = availableWidth * 0.75;  // Frame inner area
        double contentHeight = availableHeight * 0.75;
        
        double usableWidth = contentWidth - 60;   // Side margins
        double usableHeight = contentHeight - 180; // Header + footer + padding
        
        // Calculate max cell size that fits
        int maxCellWidth = (int) (usableWidth / innerCols);
        int maxCellHeight = (int) (usableHeight / innerRows);
        
        // Use smaller dimension to keep cells square
        int cellSize = Math.min(maxCellWidth, maxCellHeight);
        
        // Clamp to min/max bounds
        return Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, cellSize));
    }

    @Override
    protected Map<String, Object> getGameResult() {
        return game.getResult();
    }

    @Override
    protected Map<String, Object> saveGameState() {
        return game.saveState();
    }

    @Override
    protected String getPuzzleTypeName() {
        return "Maze";
    }

    // ==================== MAZE-SPECIFIC METHODS ====================

    /**
     * Handle keyboard input for maze navigation
     */
    private void handleKeyPress(KeyEvent event) {
        String key = event.getCode().toString();
        boolean moved = game.processInput(key);
        
        if (moved) {
            renderGame();
            if (game.isGameOver()) {
                handleGameComplete(true);
            }
        }
        event.consume();
    }
}