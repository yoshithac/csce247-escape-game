package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Tests for the Player class.
 * Verifies correct behavior of constructors and public fields.
 * Ensures player position is properly initialized and can be modified.
 */
public class PlayerTest {

    /**
     * Tests the default constructor.
     * Verifies that a Player object is created with default position (0, 0).
     */
    
    @Test
    public void testDefaultConstructor_initializesToZeroPosition() {
        Player player = new Player();

        assertNotNull("Player instance should not be null", player);
        assertEquals("Default row should be 0", 0, player.row);
        assertEquals("Default column should be 0", 0, player.col);
    }

    /**
     * Tests the parameterized constructor.
     * Verifies that a Player initialized with specific coordinates
     * correctly stores the provided row and column values.
     */

    @Test
    public void testParameterizedConstructor_setsRowAndCol() {
        Player player = new Player(2, 5);

        assertEquals("Row should match constructor value", 2, player.row);
        assertEquals("Column should match constructor value", 5, player.col);
    }

    /**
     * Tests manually changing the player's position.
     * Ensures that updating the row and column fields directly
     * reflects the new player position.
     */
    @Test
    public void testPositionChange_updatesRowAndCol() {
        Player player = new Player(1, 3);

        // Change position
        player.row = 4;
        player.col = 7;

        assertEquals("Row should update to new value", 4, player.row);
        assertEquals("Column should update to new value", 7, player.col);
    }
}
