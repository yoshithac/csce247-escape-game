package com.model;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for LeaderboardEntry entity
 * Each test method contains exactly one assertion
 */
public class LeaderboardEntryTest {
    
    private LeaderboardEntry entry;
    
    @Before
    public void setUp() {
        entry = new LeaderboardEntry("user1", "John Doe", 500, 5);
    }
    
    @Test
    public void testConstructorSetsUserId() {
        assertEquals("user1", entry.getUserId());
    }
    
    @Test
    public void testConstructorSetsUserName() {
        assertEquals("John Doe", entry.getUserName());
    }
    
    @Test
    public void testConstructorSetsTotalScore() {
        assertEquals(500, entry.getTotalScore());
    }
    
    @Test
    public void testConstructorSetsPuzzlesCompleted() {
        assertEquals(5, entry.getPuzzlesCompleted());
    }
    
    @Test
    public void testConstructorSetsLastUpdated() {
        assertNotNull(entry.getLastUpdated());
    }
    
    @Test
    public void testDefaultConstructorSetsLastUpdated() {
        LeaderboardEntry defaultEntry = new LeaderboardEntry();
        assertNotNull(defaultEntry.getLastUpdated());
    }
    
    @Test
    public void testSetUserId() {
        entry.setUserId("user2");
        assertEquals("user2", entry.getUserId());
    }
    
    @Test
    public void testSetUserName() {
        entry.setUserName("Jane Smith");
        assertEquals("Jane Smith", entry.getUserName());
    }
    
    @Test
    public void testSetTotalScore() {
        entry.setTotalScore(1000);
        assertEquals(1000, entry.getTotalScore());
    }
    
    @Test
    public void testSetPuzzlesCompleted() {
        entry.setPuzzlesCompleted(10);
        assertEquals(10, entry.getPuzzlesCompleted());
    }
    
    @Test
    public void testSetLastUpdated() {
        LocalDateTime testTime = LocalDateTime.now();
        entry.setLastUpdated(testTime);
        assertEquals(testTime, entry.getLastUpdated());
    }

    @Test
    public void testToStringContainsTotalScore() {
        assertTrue(entry.toString().contains("500"));
    }
}
