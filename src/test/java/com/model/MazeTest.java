package com.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for Maze entity
 * Each test method contains exactly one assertion
 * CORRECTED - Maze is a simple data entity with only getters/setters
 */
public class MazeTest {
    
    private Maze maze;
    private int[][] testMazeData;
    private Position start;
    private Position end;
    
    @Before
    public void setUp() {
        testMazeData = new int[][]{
            {0, 1, 0, 0, 0},
            {0, 1, 0, 1, 0},
            {0, 0, 0, 1, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 0, 1, 0}
        };
        start = new Position(0, 0);
        end = new Position(4, 4);
        maze = new Maze(5, 5, testMazeData, start, end);
    }
    
    // ===== Constructor Tests =====
    
    @Test
    public void testDefaultConstructorCreatesInstance() {
        Maze defaultMaze = new Maze();
        assertNotNull(defaultMaze);
    }
    
    @Test
    public void testParameterizedConstructorSetsWidth() {
        assertEquals(5, maze.getWidth());
    }
    
    @Test
    public void testParameterizedConstructorSetsHeight() {
        assertEquals(5, maze.getHeight());
    }
    
    @Test
    public void testParameterizedConstructorSetsMazeData() {
        assertEquals(testMazeData, maze.getMazeData());
    }
    
    @Test
    public void testParameterizedConstructorSetsStart() {
        assertEquals(start, maze.getStart());
    }
    
    @Test
    public void testParameterizedConstructorSetsEnd() {
        assertEquals(end, maze.getEnd());
    }
    
    // ===== Getter Tests =====
    
    @Test
    public void testGetWidthReturnsCorrectValue() {
        assertEquals(5, maze.getWidth());
    }
    
    @Test
    public void testGetHeightReturnsCorrectValue() {
        assertEquals(5, maze.getHeight());
    }
    
    @Test
    public void testGetMazeDataReturnsCorrectArray() {
        assertArrayEquals(testMazeData, maze.getMazeData());
    }
    
    @Test
    public void testGetMazeDataReturnsNonNull() {
        assertNotNull(maze.getMazeData());
    }
    
    @Test
    public void testGetStartReturnsCorrectPosition() {
        assertEquals(start, maze.getStart());
    }
    
    @Test
    public void testGetStartReturnsNonNull() {
        assertNotNull(maze.getStart());
    }
    
    @Test
    public void testGetEndReturnsCorrectPosition() {
        assertEquals(end, maze.getEnd());
    }
    
    @Test
    public void testGetEndReturnsNonNull() {
        assertNotNull(maze.getEnd());
    }
    
    // ===== Setter Tests =====
    
    @Test
    public void testSetWidthUpdatesWidth() {
        maze.setWidth(10);
        assertEquals(10, maze.getWidth());
    }
    
    @Test
    public void testSetWidthAcceptsZero() {
        maze.setWidth(0);
        assertEquals(0, maze.getWidth());
    }
    
    @Test
    public void testSetWidthAcceptsLargeValue() {
        maze.setWidth(100);
        assertEquals(100, maze.getWidth());
    }
    
    @Test
    public void testSetHeightUpdatesHeight() {
        maze.setHeight(10);
        assertEquals(10, maze.getHeight());
    }
    
    @Test
    public void testSetHeightAcceptsZero() {
        maze.setHeight(0);
        assertEquals(0, maze.getHeight());
    }
    
    @Test
    public void testSetHeightAcceptsLargeValue() {
        maze.setHeight(100);
        assertEquals(100, maze.getHeight());
    }
    
    @Test
    public void testSetMazeDataUpdatesData() {
        int[][] newData = new int[][]{{0, 1}, {1, 0}};
        maze.setMazeData(newData);
        assertEquals(newData, maze.getMazeData());
    }
    
    @Test
    public void testSetMazeDataAcceptsNull() {
        maze.setMazeData(null);
        assertNull(maze.getMazeData());
    }
    
    @Test
    public void testSetMazeDataAcceptsEmptyArray() {
        int[][] emptyData = new int[0][0];
        maze.setMazeData(emptyData);
        assertEquals(0, maze.getMazeData().length);
    }
    
    @Test
    public void testSetStartUpdatesStartPosition() {
        Position newStart = new Position(1, 1);
        maze.setStart(newStart);
        assertEquals(newStart, maze.getStart());
    }
    
    @Test
    public void testSetStartAcceptsNull() {
        maze.setStart(null);
        assertNull(maze.getStart());
    }
    
    @Test
    public void testSetStartAcceptsDifferentPosition() {
        Position newStart = new Position(2, 3);
        maze.setStart(newStart);
        assertEquals(2, maze.getStart().getRow());
    }
    
    @Test
    public void testSetEndUpdatesEndPosition() {
        Position newEnd = new Position(3, 3);
        maze.setEnd(newEnd);
        assertEquals(newEnd, maze.getEnd());
    }
    
    @Test
    public void testSetEndAcceptsNull() {
        maze.setEnd(null);
        assertNull(maze.getEnd());
    }
    
    @Test
    public void testSetEndAcceptsDifferentPosition() {
        Position newEnd = new Position(4, 2);
        maze.setEnd(newEnd);
        assertEquals(2, maze.getEnd().getCol());
    }
    
    // ===== MazeData Content Tests =====
    
    @Test
    public void testMazeDataContainsCorrectPathAtStart() {
        assertEquals(0, maze.getMazeData()[0][0]);
    }
    
    @Test
    public void testMazeDataContainsCorrectWallAt01() {
        assertEquals(1, maze.getMazeData()[0][1]);
    }
    
    @Test
    public void testMazeDataHasCorrectNumberOfRows() {
        assertEquals(5, maze.getMazeData().length);
    }
    
    @Test
    public void testMazeDataHasCorrectNumberOfCols() {
        assertEquals(5, maze.getMazeData()[0].length);
    }
    
    // ===== Position Relationship Tests =====
    
    @Test
    public void testStartAndEndAreDifferentObjects() {
        assertNotSame(maze.getStart(), maze.getEnd());
    }
    
    @Test
    public void testStartPositionRowIsValid() {
        assertTrue(maze.getStart().getRow() >= 0);
    }
    
    @Test
    public void testStartPositionColIsValid() {
        assertTrue(maze.getStart().getCol() >= 0);
    }
    
    @Test
    public void testEndPositionRowIsValid() {
        assertTrue(maze.getEnd().getRow() >= 0);
    }
    
    @Test
    public void testEndPositionColIsValid() {
        assertTrue(maze.getEnd().getCol() >= 0);
    }
    
    // ===== Multiple Setter Call Tests =====
    
    @Test
    public void testMultipleSetWidthCalls() {
        maze.setWidth(7);
        maze.setWidth(12);
        assertEquals(12, maze.getWidth());
    }
    
    @Test
    public void testMultipleSetHeightCalls() {
        maze.setHeight(8);
        maze.setHeight(15);
        assertEquals(15, maze.getHeight());
    }
    
    @Test
    public void testMultipleSetStartCalls() {
        Position pos1 = new Position(1, 1);
        Position pos2 = new Position(2, 2);
        maze.setStart(pos1);
        maze.setStart(pos2);
        assertEquals(pos2, maze.getStart());
    }
    
    @Test
    public void testMultipleSetEndCalls() {
        Position pos1 = new Position(3, 3);
        Position pos2 = new Position(4, 4);
        maze.setEnd(pos1);
        maze.setEnd(pos2);
        assertEquals(pos2, maze.getEnd());
    }
}
