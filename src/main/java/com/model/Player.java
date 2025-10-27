package com.model;

/**
 * Player entity for maze games
 * Represents player's current position in the maze
 */
public class Player {
    public int row;
    public int col;
    
    public Player() {}
    
    /**
     * Constructor with position
     * @param row
     * @param col
     */
    public Player(int row, int col) {
        this.row = row;
        this.col = col;
    }
}