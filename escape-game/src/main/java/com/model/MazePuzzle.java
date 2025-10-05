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


    /**
     * Constructor for MazePuzzle
     */
    public MazePuzzle() {
        playerPosition = new Point(0, 0);
        startArea = new Point(0, 0);
        endArea = new Point(5, 5);
    }

    /**
     * Creates a simple maze layout
     */
    private void simpleMaze() {
        
    }

    /**
     * Creates a trap maze layout
     */
    private void trapMaze() {
        
    }
}
