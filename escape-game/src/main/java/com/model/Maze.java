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
    
    public Maze(int width, int height, int[][] mazeData, Position start, Position end) {
        this.width = width;
        this.height = height;
        this.mazeData = mazeData;
        this.start = start;
        this.end = end;
    }
    
    // Getters and Setters
    public int getWidth() { 
        return width; 
    }
    
    public void setWidth(int width) { 
        this.width = width; 
    }
    
    public int getHeight() { 
        return height; 
    }
    
    public void setHeight(int height) { 
        this.height = height; 
    }
    
    public int[][] getMazeData() { 
        return mazeData; 
    }
    
    public void setMazeData(int[][] mazeData) { 
        this.mazeData = mazeData; 
    }
    
    public Position getStart() { 
        return start; 
    }
    
    public void setStart(Position start) { 
        this.start = start; 
    }
    
    public Position getEnd() { 
        return end; 
    }
    
    public void setEnd(Position end) { 
        this.end = end; 
    }
}

