package com.model;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for Player entity
 * Each test method contains exactly one assertion
 *
 * Ensures that the {@link Player} class correctly initializes
 * and updates its row and column position fields.
 */
public class PlayerTest {
    
    private Player player;
    
    /**
     * Initializes a Player instance with test coordinates
     * before each test runs.
     */
    @Before
    public void setUp() {
        player = new Player(2, 3);
    }
    
    @Test
    public void testConstructorSetsRow() {
        assertEquals(2, player.row);
    }
    
    @Test
    public void testConstructorSetsCol() {
        assertEquals(3, player.col);
    }
    
    @Test
    public void testSetRow() {
        player.row = 5;
        assertEquals(5, player.row);
    }
    
    @Test
    public void testSetCol() {
        player.col = 7;
        assertEquals(7, player.col);
    }
}
