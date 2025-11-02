package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Tests for LeaderboardService
 * - constructor
 * - getTopPlayers()
 * - getUserRank()
 * - getUserEntry()
 * - getFullLeaderboard()
 */
public class LeaderboardServiceTest {

    /**
     * Stub class that mimics GameDataFacade behavior without subclassing.
     */
    private static class StubGameDataFacade {
        private final List<LeaderboardEntry> mockLeaderboard;
        private final Map<String, Integer> userRanks;

        public StubGameDataFacade() {
            mockLeaderboard = new ArrayList<>();
            mockLeaderboard.add(new LeaderboardEntry("u1", "Alice", 300, 5));
            mockLeaderboard.add(new LeaderboardEntry("u2", "Bob", 250, 3));
            mockLeaderboard.add(new LeaderboardEntry("u3", "Charlie", 100, 1));

            userRanks = new HashMap<>();
            userRanks.put("u1", 1);
            userRanks.put("u2", 2);
            userRanks.put("u3", 3);
        }

        public List<LeaderboardEntry> getLeaderboard(int limit) {
            if (limit >= mockLeaderboard.size()) {
                return new ArrayList<>(mockLeaderboard);
            }
            return new ArrayList<>(mockLeaderboard.subList(0, limit));
        }

        public int getUserRank(String userId) {
            return userRanks.getOrDefault(userId, -1);
        }
    }


    /**
     * Helper to replace the singleton GameDataFacade in LeaderboardService with our stub.
     */
    private LeaderboardService createServiceWithStub() {
        try {
            java.lang.reflect.Field instanceField = GameDataFacade.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, new StubGameDataFacade());
        } catch (Exception e) {
            fail("Failed to inject StubGameDataFacade: " + e.getMessage());
        }

        return new LeaderboardService();
    }


    @Test
    public void testConstructor_createsInstanceWithoutError() {
        LeaderboardService service = createServiceWithStub();
        assertNotNull("LeaderboardService should be created successfully", service);
    }

    @Test
    public void testGetTopPlayers_returnsLimitedList() {
        LeaderboardService service = createServiceWithStub();
        List<LeaderboardEntry> result = service.getTopPlayers(2);
        assertEquals("Should return 2 top players", 2, result.size());
        assertEquals("First player should be Alice", "Alice", result.get(0).getUserName());
    }

    @Test
    public void testGetUserRank_existingUser_returnsCorrectRank() {
        LeaderboardService service = createServiceWithStub();
        int rank = service.getUserRank("u2");
        assertEquals("Bob should be rank 2", 2, rank);
    }

    @Test
    public void testGetUserRank_nonExistingUser_returnsMinusOne() {
        LeaderboardService service = createServiceWithStub();
        int rank = service.getUserRank("unknown");
        assertEquals("Unknown user should return -1", -1, rank);
    }

    @Test
    public void testGetUserEntry_existingUser_returnsEntry() {
        LeaderboardService service = createServiceWithStub();
        LeaderboardEntry entry = service.getUserEntry("u3");
        assertNotNull("User entry should not be null", entry);
        assertEquals("Should return Charlie’s entry", "Charlie", entry.getUserName());
    }

    @Test
    public void testGetUserEntry_nonExistingUser_returnsNull() {
        LeaderboardService service = createServiceWithStub();
        LeaderboardEntry entry = service.getUserEntry("nope");
        assertNull("Should return null for non-existing user", entry);
    }

    @Test
    public void testGetFullLeaderboard_returnsAllEntries() {
        LeaderboardService service = createServiceWithStub();
        List<LeaderboardEntry> result = service.getFullLeaderboard();
        assertEquals("Should return all leaderboard entries", 3, result.size());
    }
}
