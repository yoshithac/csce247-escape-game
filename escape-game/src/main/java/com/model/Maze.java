<<<<<<< HEAD
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

=======
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
     * Constructs a new {@code Maze} with specified dimensions, layout, and start/end positions.
     *
     * @param width the number of columns in the maze
     * @param height the number of rows in the maze
     * @param mazeData a 2D array representing the maze structure (0 = path, 1 = wall)
     * @param start the starting position in the maze
     * @param end the ending position in the maze
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
     * Returns the width (number of columns) of the maze.
     *
     * @return the maze width
     */
    public int getWidth() { 
        return width; 
    }

    /**
     * Sets the width (number of columns) of the maze.
     *
     * @param width the maze width
     */
    public void setWidth(int width) { 
        this.width = width; 
    }

    /**
     * Returns the height (number of rows) of the maze.
     *
     * @return the maze height
     */
    public int getHeight() { 
        return height; 
    }

    /**
     * Sets the height (number of rows) of the maze.
     *
     * @param height the maze height
     */
    public void setHeight(int height) { 
        this.height = height; 
    }

    /**
     * Returns the 2D array representing the maze structure.
     *
     * @return the maze data grid (0 = path, 1 = wall)
     */
    public int[][] getMazeData() { 
        return mazeData; 
    }

    /**
     * Sets the 2D maze structure.
     *
     * @param mazeData a 2D array representing the maze layout (0 = path, 1 = wall)
     */
    public void setMazeData(int[][] mazeData) { 
        this.mazeData = mazeData; 
    }

    /**
     * Returns the starting position of the maze.
     *
     * @return the start position
     */
    public Position getStart() { 
        return start; 
    }

    /**
     * Sets the starting position of the maze.
     *
     * @param start the start position
     */
    public void setStart(Position start) { 
        this.start = start; 
    }

    /**
     * Returns the ending (goal) position of the maze.
     *
     * @return the end position
     */
    public Position getEnd() { 
        return end; 
    }

    /**
     * Sets the ending (goal) position of the maze.
     *
     * @param end the end position
     */
    public void setEnd(Position end) { 
        this.end = end; 
    }
}
>>>>>>> main
