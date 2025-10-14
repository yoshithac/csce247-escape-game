package com.model;
import java.awt.Point;

/*
 * Represents a maze puzzle in the escape game
 * @author We're Getting an A
 */
public class MazePuzzle {
    private Movement movements;
    private Point playerPosition;
    private Point startArea;
    private Point endArea;

    //console ui mazes
    char[][] maze;
    static char[][] simpleMaze = {
        {'#','#','#','#','#','#','#'},
        {'#','P',' ','#',' ','E','#'},
        {'#',' ',' ','#',' ',' ','#'},
        {'#',' ','#','#',' ','#','#'},
        {'#',' ',' ',' ',' ',' ','#'},
        {'#','#','#','#','#','#','#'}};
    static char[][] trapMaze = {
        {'#','#','#','#','#','#','#','#','#'},
        {'#','P',' ',' ',' ','#',' ','E','#'},
        {'#',' ','#','#',' ','#',' ','#','#'},
        {'#',' ','#','#',' ',' ',' ',' ','#'},
        {'#',' ','#','#','#','#','#',' ','#'},
        {'#',' ',' ',' ',' ',' ','#',' ','#'},
        {'#','#','#','#','#',' ','#',' ','#'},
        {'#',' ',' ',' ',' ',' ',' ',' ','#'},
        {'#','#','#','#','#','#','#','#','#'}};

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
        if (maze[newRow][newCol] == '#') {
            System.out.println("You hit a wall!");
            return;
        }
        maze[playerPosition.x][playerPosition.y] = ' ';
        playerPosition.x = newRow;
        playerPosition.y = newCol;
        if (maze[playerPosition.x][playerPosition.y] != 'E')
            maze[playerPosition.x][playerPosition.y] = 'P';
    }
}
