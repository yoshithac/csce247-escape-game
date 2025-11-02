package com.model;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * JUnit tests for LeaderboardEntry:
 */
public class LeaderboardEntryTest {

    /**
     * Helper: returns true if candidate is between before and after inclusive.
     */
    private static boolean isBetweenInclusive(LocalDateTime candidate, LocalDateTime before, LocalDateTime after) {
        return (!candidate.isBefore(before)) && (!candidate.isAfter(after));
    }

    @Test
    public void testDefaultConstructor_initializesLastUpdated_andDefaultsOtherFields() {
        LocalDateTime before = LocalDateTime.now();
        LeaderboardEntry entry = new LeaderboardEntry();
        LocalDateTime after = LocalDateTime.now();

        // lastUpdated should be set and within the create window
        assertNotNull("lastUpdated should not be null after default construction", entry.getLastUpdated());
        assertTrue("lastUpdated should be set to a timestamp between before and after construction",
                isBetweenInclusive(entry.getLastUpdated(), before, after));

        // other fields should have their default values (null or 0)
        assertNull("userId should be null by default", entry.getUserId());
        assertNull("userName should be null by default", entry.getUserName());
        assertEquals("totalScore should default to 0", 0, entry.getTotalScore());
        assertEquals("puzzlesCompleted should default to 0", 0, entry.getPuzzlesCompleted());
    }

    @Test
    public void testParameterizedConstructor_setsFields_andInitializesLastUpdated() {
        LocalDateTime before = LocalDateTime.now();

        LeaderboardEntry entry = new LeaderboardEntry("u42", "Alice", 120, 3);

        LocalDateTime after = LocalDateTime.now();

        // verify fields set by constructor
        assertEquals("userId should be set by constructor", "u42", entry.getUserId());
        assertEquals("userName should be set by constructor", "Alice", entry.getUserName());
        assertEquals("totalScore should be set by constructor", 120, entry.getTotalScore());
        assertEquals("puzzlesCompleted should be set by constructor", 3, entry.getPuzzlesCompleted());

        // lastUpdated must have been initialized by the default constructor call
        assertNotNull("lastUpdated should not be null after parameterized construction", entry.getLastUpdated());
        assertTrue("lastUpdated should be within the construction time window",
                isBetweenInclusive(entry.getLastUpdated(), before, after));
    }

    @Test
    public void testToString_returnsFormattedString_withAllFields() {
        LeaderboardEntry entry = new LeaderboardEntry("u1", "Bob", 250, 7);

        String expected = "Bob: 250 pts (7 puzzles)";
        assertEquals("toString should match the expected format", expected, entry.toString());
    }

    @Test
    public void testToString_handlesNullUserName_gracefully() {
        // default constructor leaves userName null and totals at 0
        LeaderboardEntry entry = new LeaderboardEntry();

        // Expected: String.format will print "null" for a null userName
        String expected = "null: 0 pts (0 puzzles)";
        assertEquals("toString should include 'null' when userName is null", expected, entry.toString());
    }
}
