package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 * Tests for Maze class.
 * Tests all non-getter/setter methods (constructors).
 */
public class MazeTest {

    @Test
    public void testDefaultConstructor_initializesToNullsAndZeros() {
        Maze maze = new Maze();

        // Verify default values
        assertEquals("Default width should be 0", 0, maze.getWidth());
        assertEquals("Default height should be 0", 0, maze.getHeight());
        assertNull("Default mazeData should be null", maze.getMazeData());
        assertNull("Default start position should be null", maze.getStart());
        assertNull("Default end position should be null", maze.getEnd());
    }

    @Test
    public void testParameterizedConstructor_setsAllFields() {
        int[][] grid = {
            {0, 1, 0},
            {0, 0, 1}
        };
        Position start = new Position(0, 0);
        Position end = new Position(1, 2);

        Maze maze = new Maze(3, 2, grid, start, end);

        assertEquals("Width should match constructor value", 3, maze.getWidth());
        assertEquals("Height should match constructor value", 2, maze.getHeight());
        assertSame("MazeData reference should match constructor value", grid, maze.getMazeData());
        assertSame("Start position should match constructor value", start, maze.getStart());
        assertSame("End position should match constructor value", end, maze.getEnd());
    }
}
