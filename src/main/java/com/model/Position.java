package com.model;

/**
 * 2D position (row, col) used for player/cell coordinates
 */
public class Position {
    private int row;
    private int col;
    
    public Position() {}
    
    /**
     * Constructor with row and column
     * @param row
     * @param col
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Get row
     * @return int row
     */
    public int getRow() { 
        return row; 
    }
    
    /**
     * Set row
     * @param row
     */
    public void setRow(int row) { 
        this.row = row; 
    }
    
    /**
     * Get column
     * @return int col
     */
    public int getCol() { 
        return col; 
    }
    
    /**
     * Set column
     * @param col
     */
    public void setCol(int col) { 
        this.col = col; 
    }
    
    /**
     * Override equals for position comparison
     * @param obj
     * @return boolean indicating equality
     * @Override
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }
    
    /**
     * Override hashCode
     * @return int hash code
     * @Override
     */
    public int hashCode() {
        return 31 * row + col;
    }
}
