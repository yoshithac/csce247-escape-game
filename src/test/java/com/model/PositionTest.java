package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for the Position class.
 * Verifies correct behavior of constructors, getters/setters,
 * and equality/hashCode methods.
 */
public class PositionTest {

    /**
     * Tests the default constructor.
     * Ensures that a new Position object initializes with default (0, 0).
     */
    @Test
    public void testDefaultConstructor_initializesToZero() {
        Position pos = new Position();

        assertNotNull("Position instance should not be null", pos);
        assertEquals("Default row should be 0", 0, pos.getRow());
        assertEquals("Default column should be 0", 0, pos.getCol());
    }

    /**
     * Tests the parameterized constructor.
     * Verifies that row and column are set correctly.
     */
    @Test
    public void testParameterizedConstructor_setsCorrectValues() {
        Position pos = new Position(3, 6);

        assertEquals("Row should match constructor value", 3, pos.getRow());
        assertEquals("Column should match constructor value", 6, pos.getCol());
    }

    /**
     * Tests the setters and getters for row and column.
     * Verifies that setting values updates the position.
     */
    @Test
    public void testSetters_updateValuesCorrectly() {
        Position pos = new Position();
        pos.setRow(5);
        pos.setCol(9);

        assertEquals("Row should be updated to 5", 5, pos.getRow());
        assertEquals("Column should be updated to 9", 9, pos.getCol());
    }

    /**
     * Tests equality between two Position objects with same coordinates.
     * Verifies that equals() returns true and hashCodes match.
     */
    @Test
    public void testEquals_sameValues_returnsTrue() {
        Position p1 = new Position(2, 3);
        Position p2 = new Position(2, 3);

        assertTrue("Positions with same coordinates should be equal", p1.equals(p2));
        assertEquals("Equal positions should have the same hashCode", p1.hashCode(), p2.hashCode());
    }

    /**
     * Tests equality between two Position objects with different coordinates.
     * Verifies that equals() returns false.
     */
    @Test
    public void testEquals_differentValues_returnsFalse() {
        Position p1 = new Position(1, 4);
        Position p2 = new Position(2, 5);

        assertFalse("Positions with different coordinates should not be equal", p1.equals(p2));
    }

    /**
     * Tests equality comparison with null and a different object type.
     * Verifies that equals() correctly returns false.
     */
    @Test
    public void testEquals_withNullAndDifferentType_returnsFalse() {
        Position p1 = new Position(3, 7);

        assertFalse("Position should not be equal to null", p1.equals(null));
        assertFalse("Position should not be equal to a different type", p1.equals("NotAPosition"));
    }
}
