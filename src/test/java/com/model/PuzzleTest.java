package com.model;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for the Puzzle class.
 * Verifies constructors, getters/setters, toString and basic data behavior.
 */
public class PuzzleTest {

    /**
     * Tests the default constructor.
     * Verifies that a Puzzle instance can be created and fields are null by default.
     */
    @Test
    public void testDefaultConstructor_initializesToNulls() {
        Puzzle p = new Puzzle();

        assertNotNull("Puzzle instance should not be null", p);
        assertNull("puzzleId should be null by default", p.getPuzzleId());
        assertNull("puzzleType should be null by default", p.getPuzzleType());
        assertNull("difficulty should be null by default", p.getDifficulty());
        assertNull("title should be null by default", p.getTitle());
        assertNull("description should be null by default", p.getDescription());
        assertNull("data should be null by default", p.getData());
    }

    /**
     * Tests the parameterized constructor sets all fields and preserves data reference.
     */
    @Test
    public void testParameterizedConstructor_setsAllFieldsAndDataReference() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");

        Puzzle p = new Puzzle("p1", "MAZE", "EASY", "Title", "Desc", map);

        assertEquals("puzzleId should match constructor value", "p1", p.getPuzzleId());
        assertEquals("puzzleType should match constructor value", "MAZE", p.getPuzzleType());
        assertEquals("difficulty should match constructor value", "EASY", p.getDifficulty());
        assertEquals("title should match constructor value", "Title", p.getTitle());
        assertEquals("description should match constructor value", "Desc", p.getDescription());

        // data reference should be the same object passed in
        assertSame("data reference should match the map provided to constructor", map, p.getData());
        assertEquals("data map should contain the key/value", "value", p.getData().get("key"));
    }

    /**
     * Tests setters and getters for all fields.
     */
    @Test
    public void testSettersAndGetters_updateAndReturnValues() {
        Puzzle p = new Puzzle();

        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);

        p.setPuzzleId("xx");
        p.setPuzzleType("RIDDLE");
        p.setDifficulty("HARD");
        p.setTitle("My Puzzle");
        p.setDescription("A short desc");
        p.setData(map);

        assertEquals("puzzleId should return set value", "xx", p.getPuzzleId());
        assertEquals("puzzleType should return set value", "RIDDLE", p.getPuzzleType());
        assertEquals("difficulty should return set value", "HARD", p.getDifficulty());
        assertEquals("title should return set value", "My Puzzle", p.getTitle());
        assertEquals("description should return set value", "A short desc", p.getDescription());
        assertSame("data should return the same map reference set", map, p.getData());
    }

    /**
     * Tests toString returns a formatted summary including type, title, difficulty and id.
     */
    @Test
    public void testToString_containsTypeTitleDifficultyAndId() {
        Puzzle p = new Puzzle("id42", "ANAGRAM", "MEDIUM", "WordPlay", "desc", null);
        String s = p.toString();

        assertTrue("toString should contain puzzle type", s.contains("ANAGRAM"));
        assertTrue("toString should contain title", s.contains("WordPlay"));
        assertTrue("toString should contain difficulty", s.contains("MEDIUM"));
        assertTrue("toString should contain id", s.contains("id42"));
    }

    /**
     * Tests behavior when data is modified externally (verify same reference).
     */
    @Test
    public void testExternalModificationOfData_reflectsInPuzzle() {
        Map<String, Object> map = new HashMap<>();
        Puzzle p = new Puzzle("id", "TYPE", "EASY", "T", "D", map);

        // modify the original map after constructing puzzle
        map.put("newKey", 123);

        assertNotNull("data should not be null after modification", p.getData());
        assertEquals("Puzzle data should reflect external changes", 123, p.getData().get("newKey"));
    }
}
