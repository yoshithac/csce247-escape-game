package com.escapegame.controller;

import com.model.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

import java.util.Map;

/**
 * MatchingGameController - UI Controller for Matching Puzzle View
 * 
 * Extends BasePuzzleController for common functionality:
 * - Timer management
 * - Background setup
 * - Save/back navigation
 * - Game completion handling
 * 
 * This controller handles:
 * - Card grid rendering
 * - Card click/flip logic
 * - Match detection animation
 */
public class MatchingGameController extends BasePuzzleController {

    // Additional FXML components specific to Matching
    @FXML private Label statsLabel;
    @FXML private GridPane cardGrid;

    // Game model
    private MatchingGame game;
    
    // Game state references (cached from game state for rendering)
    private String[][] board;
    private boolean[][] matched;
    private Position firstCard;
    private Position secondCard;
    private int rows;
    private int cols;

    // ==================== ABSTRACT METHOD IMPLEMENTATIONS ====================

    @Override
    protected void initializeGame() {
        game = new MatchingGame();
        
        // Restore or initialize
        restoreOrInitialize(
            () -> game.restoreState(getSavedGameState()),
            () -> game.initialize(puzzle.getData())
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void renderGame() {
        Map<String, Object> state = game.getGameState();
        board = (String[][]) state.get("board");
        matched = (boolean[][]) state.get("matched");
        firstCard = (Position) state.get("firstCard");
        secondCard = (Position) state.get("secondCard");
        int moveCount = (int) state.get("moveCount");
        
        rows = board.length;
        cols = board[0].length;
        
        // Count matched pairs
        int matchedCount = countMatchedCards();
        int totalPairs = (rows * cols) / 2;
        
        // Update stats
        if (statsLabel != null) {
            statsLabel.setText("Moves: " + moveCount + " | Matched: " + (matchedCount/2) + "/" + totalPairs);
        }
        
        // Render card grid
        cardGrid.getChildren().clear();
        int cardSize = calculateCardSize();
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Button cardBtn = createCardButton(r, c, cardSize);
                cardGrid.add(cardBtn, c, r);
            }
        }
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
        return "Matching Game";
    }

    // ==================== MATCHING-SPECIFIC METHODS ====================

    /**
     * Count how many cards have been matched
     */
    private int countMatchedCards() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (matched[r][c]) count++;
            }
        }
        return count;
    }

    /**
     * Calculate optimal card size based on available content area
     */
    private int calculateCardSize() {
        double width = rootPane.getWidth();
        double height = rootPane.getHeight();
        
        if (width <= 0 || height <= 0) {
            return 70; // Default fallback
        }
        
        // Calculate available content area (after frame padding)
        double contentWidth = width * 0.70;  // Account for frame padding
        double contentHeight = height * 0.66;
        
        // Reserve space for header and footer
        double availableHeight = contentHeight - 190;
        double availableWidth = contentWidth - 40;
        
        // Calculate max card size that fits
        int gap = 10;
        int maxCardWidth = (int) ((availableWidth - (cols - 1) * gap) / cols);
        int maxCardHeight = (int) ((availableHeight - (rows - 1) * gap) / rows);
        
        // Use smaller dimension to keep cards square, clamped to bounds
        int cardSize = Math.min(maxCardWidth, maxCardHeight);
        return Math.max(50, Math.min(90, cardSize));
    }

    /**
     * Create a card button with proper styling
     */
    private Button createCardButton(int row, int col, int cardSize) {
        Button btn = new Button();
        btn.getStyleClass().add("card-button");
        
        // Set dynamic size
        btn.setPrefSize(cardSize, cardSize);
        btn.setMinSize(cardSize, cardSize);
        btn.setMaxSize(cardSize, cardSize);
        
        // Adjust font size based on card size
        int fontSize = Math.max(20, cardSize * 40 / 90);
        btn.setStyle("-fx-font-size: " + fontSize + "px;");
        
        // Center text/emoji
        btn.setAlignment(Pos.CENTER);
        btn.setTextAlignment(TextAlignment.CENTER);
        btn.setWrapText(false);
        
        boolean isMatched = matched[row][col];
        boolean isFaceUp = isCardFaceUp(row, col);
        
        if (isMatched) {
            btn.setText(board[row][col]);
            btn.getStyleClass().add("card-matched");
            btn.setDisable(true);
        } else if (isFaceUp) {
            btn.setText(board[row][col]);
            btn.getStyleClass().add("card-face-up");
        } else {
            btn.setText("?");
            btn.getStyleClass().add("card-face-down");
            final int r = row;
            final int c = col;
            btn.setOnAction(e -> handleCardClick(r, c));
        }
        
        return btn;
    }
    
    /**
     * Check if a card is currently face up (selected)
     */
    private boolean isCardFaceUp(int row, int col) {
        if (firstCard != null && firstCard.getRow() == row && firstCard.getCol() == col) {
            return true;
        }
        if (secondCard != null && secondCard.getRow() == row && secondCard.getCol() == col) {
            return true;
        }
        return false;
    }

    /**
     * Handle card click - flip card and check for match
     */
    private void handleCardClick(int row, int col) {
        String input = row + " " + col;
        boolean valid = game.processInput(input);
        renderGame();
        
        if (valid && game.isShowingPair()) {
            // Pause to show both cards, then clear selection
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        game.clearSelection();
                        renderGame();
                        
                        if (game.isGameOver()) {
                            handleMatchingComplete();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    /**
     * Handle matching game completion
     */
    private void handleMatchingComplete() {
        Map<String, Object> result = game.getResult();
        boolean won = (boolean) result.get("won");
        handleGameComplete(won);
    }
}
