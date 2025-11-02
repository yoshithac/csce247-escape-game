package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HintTest {

    @Test
    public void testDefaultConstructor_defaults() {
        Hint h = new Hint();
        // default constructor should create an object with null text & id and 0 priority
        assertNull("hintText should be null by default", h.getHintText());
        assertNull("puzzleId should be null by default", h.getPuzzleId());
        assertEquals("hintPriority should default to 0", 0, h.getHintPriority());
    }

    @Test
    public void testParameterizedConstructor_setsFields() {
        Hint h = new Hint("Try the left drawer", "p1", 2);
        assertEquals("hintText should be set by constructor", "Try the left drawer", h.getHintText());
        assertEquals("puzzleId should be set by constructor", "p1", h.getPuzzleId());
        assertEquals("hintPriority should be set by constructor", 2, h.getHintPriority());
    }

    @Test
    public void testCompareTo_lowerPriorityIsBeforeHigher() {
        Hint first = new Hint("First hint", "p1", 1);
        Hint second = new Hint("Second hint", "p1", 2);

        // first has lower numeric priority value -> should compare as "less than" second
        assertTrue("first.compareTo(second) should be negative when first has lower priority",
                first.compareTo(second) < 0);
        assertTrue("second.compareTo(first) should be positive when second has higher priority",
                second.compareTo(first) > 0);
    }

    @Test
    public void testCompareTo_equalPriorityIsZero() {
        Hint a = new Hint("Hint A", "p1", 3);
        Hint b = new Hint("Hint B", "p2", 3);

        assertEquals("compareTo should return 0 for equal priorities", 0, a.compareTo(b));
    }

    @Test
    public void testCompareTo_worksWithSorting() {
        Hint low = new Hint("low", "p", 5);
        Hint mid = new Hint("mid", "p", 2);
        Hint high = new Hint("high", "p", 1);

        List<Hint> list = new ArrayList<>();
        list.add(low);
        list.add(mid);
        list.add(high);

        Collections.sort(list); // uses compareTo

        // After sorting, expected order: high (1), mid (2), low (5)
        assertEquals(1, list.get(0).getHintPriority());
        assertEquals(2, list.get(1).getHintPriority());
        assertEquals(5, list.get(2).getHintPriority());
    }

    @Test
    public void testToString_returnsFormattedString() {
        Hint h = new Hint("Look under the mat", "p1", 1);
        String expected = "[Priority 1] Look under the mat";
        assertEquals("toString should match expected format", expected, h.toString());
    }

    @Test
    public void testToString_handlesNullHintText() {
        // If hintText is null, String.format will print "null"
        Hint h = new Hint(null, "p1", 4);
        String expected = "[Priority 4] null";
        assertEquals("toString should include 'null' for null hintText", expected, h.toString());
    }
}
