package com.model;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for Puzzle entity
 * Each test method contains exactly one assertion
 * Verifies that the Puzzle POJO correctly stores and exposes
 * its identifying fields, metadata, and associated data map.</p>
 */
public class PuzzleTest {
    
    private Puzzle puzzle;
    private Map<String, Object> testData;
    
    /**
     * Prepare a sample Puzzle instance used by the tests.
     * Initializes a small test data map and constructs a Puzzle.
     */
    @Before
    public void setUp() {
        testData = new HashMap<>();
        testData.put("width", 5);
        testData.put("height", 5);
        puzzle = new Puzzle("P001", "MAZE", "EASY", "Test Maze", "A simple test maze", testData);
    }
    
    @Test
    public void testConstructorSetsPuzzleId() {
        assertEquals("P001", puzzle.getPuzzleId());
    }
    
    @Test
    public void testConstructorSetsPuzzleType() {
        assertEquals("MAZE", puzzle.getPuzzleType());
    }
    
    @Test
    public void testConstructorSetsDifficulty() {
        assertEquals("EASY", puzzle.getDifficulty());
    }
    
    @Test
    public void testConstructorSetsTitle() {
        assertEquals("Test Maze", puzzle.getTitle());
    }
    
    @Test
    public void testConstructorSetsDescription() {
        assertEquals("A simple test maze", puzzle.getDescription());
    }
    
    @Test
    public void testConstructorSetsData() {
        assertEquals(testData, puzzle.getData());
    }
    
    @Test
    public void testSetPuzzleId() {
        puzzle.setPuzzleId("P002");
        assertEquals("P002", puzzle.getPuzzleId());
    }
    
    @Test
    public void testSetPuzzleType() {
        puzzle.setPuzzleType("MATCHING");
        assertEquals("MATCHING", puzzle.getPuzzleType());
    }
    
    @Test
    public void testSetDifficulty() {
        puzzle.setDifficulty("HARD");
        assertEquals("HARD", puzzle.getDifficulty());
    }
    
    @Test
    public void testSetTitle() {
        puzzle.setTitle("New Title");
        assertEquals("New Title", puzzle.getTitle());
    }
    
    @Test
    public void testSetDescription() {
        puzzle.setDescription("New Description");
        assertEquals("New Description", puzzle.getDescription());
    }
    
    @Test
    public void testSetData() {
        Map<String, Object> newData = new HashMap<>();
        newData.put("key", "value");
        puzzle.setData(newData);
        assertEquals(newData, puzzle.getData());
    }
    
    @Test
    public void testToStringContainsPuzzleId() {
        assertTrue(puzzle.toString().contains("P001"));
    }
    
    @Test
    public void testToStringContainsTitle() {
        assertTrue(puzzle.toString().contains("Test Maze"));
    }
}
