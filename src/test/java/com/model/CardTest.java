package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for Card entity
 * Each test method contains exactly one assertion
 */
public class CardTest {
    
    private Card card;
    
    @Before
    public void setUp() {
        card = new Card("C001", "🌟", "Star");
    }
    
    @Test
    public void testConstructorSetsId() {
        assertEquals("C001", card.getId());
    }
    
    @Test
    public void testConstructorSetsValue() {
        assertEquals("🌟", card.getValue());
    }
    
    @Test
    public void testConstructorSetsName() {
        assertEquals("Star", card.getName());
    }
    
    @Test
    public void testSetId() {
        card.setId("C002");
        assertEquals("C002", card.getId());
    }
    
    @Test
    public void testSetValue() {
        card.setValue("🎈");
        assertEquals("🎈", card.getValue());
    }
    
    @Test
    public void testSetName() {
        card.setName("Balloon");
        assertEquals("Balloon", card.getName());
    }
    
    @Test
    public void testToStringContainsId() {
        assertTrue(card.toString().contains("C001"));
    }
    
    @Test
    public void testToStringContainsName() {
        assertTrue(card.toString().contains("Star"));
    }
    
    @Test
    public void testToStringContainsValue() {
        assertTrue(card.toString().contains("🌟"));
    }
}
