package com.model;

/**
 * Maze entity representing a maze puzzle
 * Contains grid data and start/end positions
 */
public class Maze {
    private int width;
    private int height;
    private int[][] mazeData;  // 0 = path, 1 = wall
    private Position start;
    private Position end;
    
    public Maze() {}
    
    /**
     * Constructor with all fields
     * @param width
     * @param height
     * @param mazeData
     * @param start
     * @param end
     */
    public Maze(int width, int height, int[][] mazeData, Position start, Position end) {
        this.width = width;
        this.height = height;
        this.mazeData = mazeData;
        this.start = start;
        this.end = end;
    }
    
    // Getters and Setters
    /**
     * Get maze width
     * @return int width
     */
    public int getWidth() { 
        return width; 
    }
    
    /**
     * Set maze width
     * @param width
     */
    public void setWidth(int width) { 
        this.width = width; 
    }
    
    /**
     * Get maze height
     * @return int height
     */
    public int getHeight() { 
        return height; 
    }
    
    /**
     * Set maze height
     * @param height
     */
    public void setHeight(int height) { 
        this.height = height; 
    }
    
    /**
     * Get maze data grid
     * @return int[][] mazeData
     */
    public int[][] getMazeData() { 
        return mazeData; 
    }
    
    /**
     * Set maze data grid
     * @param mazeData
     */
    public void setMazeData(int[][] mazeData) { 
        this.mazeData = mazeData; 
    }
    
    /**
     * Get start position
     * @return Position start
     */
    public Position getStart() { 
        return start; 
    }
    
    /**
     * Set start position
     * @param start
     */
    public void setStart(Position start) { 
        this.start = start; 
    }
    
    /**
     * Get end position
     * @return Position end
     */
    public Position getEnd() { 
        return end; 
    }
    
    /**
     * Set end position
     * @param end
     */
    public void setEnd(Position end) { 
        this.end = end; 
    }
}