package com.model;
import java.awt.Point;
import java.util.Scanner;

public class MazePuzzle {
    private Point startArea;
    private Point endArea;
    private Point playerPosition;
    private int moveCount = 0;
    private int startTime;

    //console ui mazes
    char[][] maze;
    static char[][] simpleMaze = {
        {'1','1','1','1','1','1','1'},
        {'1','P','0','1','0','E','1'},
        {'1','0','0','1','0','0','1'},
        {'1','0','1','1','0','1','1'},
        {'1','0','0','0','0','0','1'},
        {'1','1','1','1','1','1','1'},
        {'1','1','1','1','1','1','1'}};
    static char[][] trapMaze = {
        {'1','1','1','1','1','1','1','1','1'},
        {'1','P','0','0','0','1','0','E','1'},
        {'1','0','1','1','0','1','0','1','1'},
        {'1','0','1','1','0','0','0','0','1'},
        {'1','0','1','1','1','1','1','0','1'},
        {'1','0','0','0','0','T','1','0','1'},
        {'1','1','1','1','1','1','1','0','1'},
        {'1','0','0','T','0','0','0','0','1'},
        {'1','1','1','1','1','1','1','1','1'}};

    /**
     * Constructor for MazePuzzle
     */
    public MazePuzzle() {
        playerPosition = new Point(1, 1);
        startArea = new Point(1, 1);
    }

    /**
     * Creates a simple maze layout
     */
    private void simpleMaze() {
        
        maze = simpleMaze;
        endArea = new Point(5, 1);
        for (char[] row : simpleMaze) {
            for (char c : row) System.out.print(c);
            System.out.println();
        }
    }

    /**
     * Creates a trap maze layout
     */
    private void trapMaze() {
        
        maze = trapMaze;
        endArea = new Point(7, 1);
    }

    /**
     * Private helper method to move the player in the maze
     * @param direction (a, w, s, d)
     */
    private void movePlayer(char direction) {
        int newRow = playerPosition.x, newCol = playerPosition.y;
        switch (direction) {
            case 'w': newRow--; break;
            case 's': newRow++; break;
            case 'a': newCol--; break;
            case 'd': newCol++; break;
            default:
                System.out.println("Use W/A/S/D to move");
                return;
        }
        if (maze[newRow][newCol] == '1') {
            System.out.println("You hit a wall");
            return;
        }

        moveCount++;
        maze[playerPosition.x][playerPosition.y] = '0';
        playerPosition.x = newRow;
        playerPosition.y = newCol;

        char current = maze[newRow][newCol];

        if (current == 'E') return; // Reached exit
        if (current == 'T') {
            System.out.println("You fell into a trap! Returning to start.");
            playerPosition.setLocation(startArea);
        }

        maze[playerPosition.x][playerPosition.y] = 'P';
    }

    /*
     * Displays the results and score upon completing the maze
     */
    private void showScore() {
        int elapsed = (int)(System.currentTimeMillis() - startTime) / 1000;
        int score = Math.max(0, 300 - (int)(elapsed * 10 + moveCount * 5));
        System.out.println("Maze Completed!");
        System.out.println("Time: " + elapsed + " seconds");
        System.out.println("Moves: " + moveCount);
        System.out.println("Score: " + score);
    }

    //tester methods for maze movement and display -- delete later
    private void displayMaze() {
        for (char[] row : maze) {
            for (char c : row) System.out.print(c);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MazePuzzle game = new MazePuzzle();

        System.out.println("Choose your maze type:");
        System.out.println("1. Simple Maze");
        System.out.println("2. Trap Maze");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();

        if (choice == 2) game.trapMaze();
        else game.simpleMaze();

        System.out.println("\nNavigate the maze using W/A/S/D. Reach 'E' to win!");
        System.out.println("Avoid traps marked as 'T'!\n");

        game.startTime = (int)System.currentTimeMillis();

        while (true) {
            game.displayMaze();
            System.out.print("Move (W/A/S/D): ");
            char move = Character.toLowerCase(sc.next().charAt(0));
            game.movePlayer(move);

            if (game.playerPosition.equals(game.endArea) || game.maze[game.playerPosition.x][game.playerPosition.y] == 'E') {
                game.displayMaze();
                game.showScore();
                break;
            }
        }

        sc.close();
    }
}
