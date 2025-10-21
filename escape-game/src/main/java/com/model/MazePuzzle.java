package com.model;
import java.awt.Point;

public class MazePuzzle {
    private Point startArea;
    private Point endArea;
    private Point playerPosition;

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
        {'1','0','0','0','0','0','1','0','1'},
        {'1','1','1','1','1','1','1','0','1'},
        {'1','0','0','0','0','0','0','0','1'},
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
        maze[playerPosition.x][playerPosition.y] = '0';
        playerPosition.x = newRow;
        playerPosition.y = newCol;
        if (maze[playerPosition.x][playerPosition.y] != 'E')
            maze[playerPosition.x][playerPosition.y] = 'P';
    }
}
