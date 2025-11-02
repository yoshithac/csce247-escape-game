package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for Hint entity
 * Each test method contains exactly one assertion
 */
public class HintTest {
    
    private Hint hint;
    
    @Before
    public void setUp() {
        hint = new Hint("Look for the exit on the right side", "P001", 1);
    }
    
    @Test
    public void testConstructorSetsHintText() {
        assertEquals("Look for the exit on the right side", hint.getHintText());
    }
    
    @Test
    public void testConstructorSetsPuzzleId() {
        assertEquals("P001", hint.getPuzzleId());
    }
    
    @Test
    public void testConstructorSetsHintPriority() {
        assertEquals(1, hint.getHintPriority());
    }
    
    @Test
    public void testSetHintText() {
        hint.setHintText("New hint");
        assertEquals("New hint", hint.getHintText());
    }
    
    @Test
    public void testSetPuzzleId() {
        hint.setPuzzleId("P002");
        assertEquals("P002", hint.getPuzzleId());
    }
    
    @Test
    public void testSetHintPriority() {
        hint.setHintPriority(2);
        assertEquals(2, hint.getHintPriority());
    }
    
    @Test
    public void testCompareToOrdersByPriority() {
        Hint hint2 = new Hint("Second hint", "P001", 2);
        assertTrue(hint.compareTo(hint2) < 0);
    }
    
    @Test
    public void testCompareToReturnsZeroForSamePriority() {
        Hint hint2 = new Hint("Another hint", "P001", 1);
        assertEquals(0, hint.compareTo(hint2));
    }
    
    @Test
    public void testToStringContainsHintText() {
        assertTrue(hint.toString().contains("Look for the exit"));
    }
    
    @Test
    public void testToStringContainsPriority() {
        assertTrue(hint.toString().contains("1"));
    }
}
