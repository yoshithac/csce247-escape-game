package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for Position entity
 * Each test method contains exactly one assertion
 */
public class PositionTest {
    
    private Position position;
    
    @Before
    public void setUp() {
        position = new Position(3, 5);
    }
    
    @Test
    public void testConstructorSetsRow() {
        assertEquals(3, position.getRow());
    }
    
    @Test
    public void testConstructorSetsCol() {
        assertEquals(5, position.getCol());
    }
    
    @Test
    public void testSetRow() {
        position.setRow(7);
        assertEquals(7, position.getRow());
    }
    
    @Test
    public void testSetCol() {
        position.setCol(9);
        assertEquals(9, position.getCol());
    }
    
    @Test
    public void testEqualsReturnsTrueForSamePosition() {
        Position other = new Position(3, 5);
        assertTrue(position.equals(other));
    }
    
    @Test
    public void testEqualsReturnsFalseForDifferentRow() {
        Position other = new Position(4, 5);
        assertFalse(position.equals(other));
    }
    
    @Test
    public void testEqualsReturnsFalseForDifferentCol() {
        Position other = new Position(3, 6);
        assertFalse(position.equals(other));
    }
    
    @Test
    public void testEqualsReturnsTrueForSameObject() {
        assertTrue(position.equals(position));
    }
    
    @Test
    public void testEqualsReturnsFalseForNull() {
        assertFalse(position.equals(null));
    }
    
    @Test
    public void testEqualsReturnsFalseForDifferentClass() {
        assertFalse(position.equals("not a position"));
    }
    
    @Test
    public void testHashCodeConsistency() {
        Position other = new Position(3, 5);
        assertEquals(position.hashCode(), other.hashCode());
    }
    /* 
    @Test
    public void testToStringContainsRow() {
        assertTrue(position.toString().contains("3"));
    }
    
    @Test
    public void testToStringContainsCol() {
        assertTrue(position.toString().contains("5"));
    } */
}
