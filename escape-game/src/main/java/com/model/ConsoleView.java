package com.model;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.speech.Speak;

/**
 * Console implementation of GameView interface
 * Provides text-based UI for terminal/console
 */
public class ConsoleView implements GameView {
    private final Scanner scanner;

    public ConsoleView(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    @Override
    public String showMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.print("\nChoice: ");
        return scanner.nextLine();
    }

    @Override
    public String showPuzzlesMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println("ROOM " + (i + 1) + ". " + options[i]);
        }
        System.out.print("\nChoice: ");
        return scanner.nextLine();
    }

    @Override
    public void displayGame(Map<String, Object> gameState, String gameType) {
        switch (gameType.toUpperCase()) {
            case "MAZE":
                displayMaze(gameState);
                break;
            case "MATCHING":
                displayMatching(gameState);
                break;
            case "CIPHER":
            case "ANAGRAM":
            case "RIDDLE":
                displayWordPuzzle(gameState);
                break;
            default:
                showMessage("Unknown game type: " + gameType);
        }
    }

    private void displayMaze(Map<String, Object> state) {
        Maze maze = (Maze) state.get("maze");
        Player player = (Player) state.get("player");
        int moves = (int) state.get("moveCount");

        System.out.println("\n" + "=".repeat(40));
        System.out.println("         MAZE GAME");
        System.out.println("=".repeat(40));
        System.out.println("Moves: " + moves);
        System.out.println();

        for (int r = 0; r < maze.getHeight(); r++) {
            for (int c = 0; c < maze.getWidth(); c++) {
                if (player.row == r && player.col == c) {
                    System.out.print("P "); // Player
                } else if (maze.getEnd().getRow() == r && maze.getEnd().getCol() == c) {
                    System.out.print("E "); // Exit
                } else if (maze.getMazeData()[r][c] == 1) {
                    System.out.print("█ "); // Wall
                } else {
                    System.out.print("  "); // Path
                }
            }
            System.out.println();
        }

        System.out.println("\nControls: W=Up, S=Down, A=Left, D=Right");
        System.out.println("Type 'save' to save and quit, 'quit' to quit without saving");
    }

    private void displayMatching(Map<String, Object> state) {
        String[][] board = (String[][]) state.get("board");
        boolean[][] matched = (boolean[][]) state.get("matched");
        Position firstCard = (Position) state.get("firstCard");
        Position secondCard = (Position) state.get("secondCard");
        int moves = (int) state.get("moveCount");
        
        // Get dimensions from board array instead of boardSize
        int rows = board.length;
        int cols = board[0].length;

        System.out.println("\n" + "=".repeat(40));
        System.out.println("         MATCHING GAME");
        System.out.println("=".repeat(40));
        System.out.println("Moves: " + moves);
        System.out.println();

        // Column headers
        System.out.print("  ");
        for (int c = 0; c < cols; c++) {
            System.out.print(c + " ");
        }
        System.out.println();

        for (int r = 0; r < rows; r++) {
            System.out.print(r + " "); // Row header
            for (int c = 0; c < cols; c++) {
                boolean isFirst = firstCard != null &&
                        firstCard.getRow() == r && firstCard.getCol() == c;
                boolean isSecond = secondCard != null &&
                        secondCard.getRow() == r && secondCard.getCol() == c;

                if (matched[r][c] || isFirst || isSecond) {
                    System.out.print(board[r][c] + " ");
                } else {
                    System.out.print("* ");
                }
            }
            System.out.println();
        }

        System.out.println("\nEnter row col (e.g., 0 1)");
        System.out.println("Type 'save' to save and quit, 'quit' to quit without saving");
    }

    private void displayWordPuzzle(Map<String, Object> state) {
        String puzzleType = (String) state.get("puzzleType");
        String prompt = (String) state.get("prompt");
        String category = (String) state.get("category");
        int attemptsUsed = (int) state.get("attemptsUsed");
        int maxAttempts = (int) state.get("maxAttempts");
        int remainingAttempts = (int) state.get("remainingAttempts");
        List<String> guesses = (List<String>) state.get("guesses");
        List<String> revealedHints = (List<String>) state.get("revealedHints");
        int availableHintsCount = (int) state.get("availableHintsCount");

        System.out.println("\n" + "=".repeat(40));
        System.out.println("         " + puzzleType + " PUZZLE");
        System.out.println("=".repeat(40));
        System.out.println("Category: " + category);
        System.out.println("Prompt: " + prompt);
        System.out.println("\nAttempts: " + attemptsUsed + "/" + maxAttempts +
                " (Remaining: " + remainingAttempts + ")");

        if (guesses != null && !guesses.isEmpty()) {
            System.out.println("\nYour guesses:");
            for (String guess : guesses) {
                System.out.println(" X " + guess);
            }
        }

        if (revealedHints != null && !revealedHints.isEmpty()) {
            System.out.println("\nHints:");
            for (String hint : revealedHints) {
                System.out.println(" * " + hint);
            }
        }

        if (availableHintsCount > 0) {
            System.out.println("\n" + availableHintsCount + " more hint(s) available (type 'HINT')");
        }

        System.out.println("\nType 'save' to save and quit, 'quit' to quit without saving");
    }

    @Override
    public void showResult(Map<String, Object> result) {
        boolean won = (boolean) result.get("won");
        long timeMs = (long) result.get("time");
        int moves = (int) result.get("moves");

        System.out.println("\n" + "=".repeat(50));
        if (won) {
            // key earned message begins
            String key = "You found one key, its been added to your inventory..";
            System.out.println(key);
            Speak.speak(key);
            
            //key earned message ends

            System.out.println(" ROOM COMPLETED! ");
        } else {
            System.out.println("         GAME OVER         ");
            if (result.containsKey("answer")) {
                System.out.println("The answer was: " + result.get("answer"));
            }
        }
        System.out.println("=".repeat(50));
        System.out.println("Time: " + (timeMs / 1000) + " seconds");
        System.out.println("Moves/Attempts: " + moves);
        if (result.containsKey("hintsUsed")) {
            System.out.println("Hints used: " + result.get("hintsUsed"));
        }
        System.out.println("=".repeat(50));
    }

    @Override
    public void clear() {
        // ANSI escape code to clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
