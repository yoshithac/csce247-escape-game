package com.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test cases for LeaderboardService using TemporaryFolder
 * Each test runs in complete isolation with temporary files
 */
public class LeaderboardServiceTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private LeaderboardService leaderboardService;
    private GameDataFacade facade;
    private File testUserFile;
    private File testGameDataFile;
    
    @Before
    public void setUp() throws IOException {
        // Create temporary test files
        testUserFile = tempFolder.newFile("users.json");
        testGameDataFile = tempFolder.newFile("gamedata.json");
        
        // Create test data
        createTestData();
        
        // Create loader and writer with test paths
        GameDataLoader testLoader = new GameDataLoader(
            testUserFile.getAbsolutePath(),
            testGameDataFile.getAbsolutePath()
        );
        
        GameDataWriter testWriter = new GameDataWriter(
            testUserFile.getAbsolutePath(),
            testGameDataFile.getAbsolutePath()
        );
        
        // Create facade and service with test dependencies
        facade = new GameDataFacade(testLoader, testWriter);
        GameDataFacade.setTestInstance(facade);
        leaderboardService = new LeaderboardService();
    }
    
    @After
    public void tearDown() {
        GameDataFacade.resetInstance();
        facade = null;
        leaderboardService = null;
    }
    
    private void createTestData() throws IOException {
        String testUsers = "[]";
        Files.write(testUserFile.toPath(), testUsers.getBytes());
        
        String testGameData = "{" +
            "\"puzzles\":[" +
                "{\"puzzleId\":\"puzzle1\",\"title\":\"Puzzle 1\",\"difficulty\":\"EASY\",\"puzzleType\":\"MAZE\"}" +
            "]," +
            "\"hints\":[]," +
            "\"userProgress\":[]," +
            "\"certificates\":[]," +
            "\"leaderboard\":[]" +
            "}";
        Files.write(testGameDataFile.toPath(), testGameData.getBytes());
    }
    
    /**
     * Helper method to create user and add them to leaderboard
     */
    private void createUserWithProgress(String userId, String firstName, String lastName, int score) {
        // Register user
        User user = new User(userId, "password", firstName, lastName, userId + "@example.com");
        facade.addUser(user);
        
        // Create progress and complete puzzle
        UserProgress progress = facade.getUserProgress(userId);
        progress.addCompletedPuzzle("puzzle1", score);
        
        // Save progress (this triggers leaderboard update)
        facade.saveUserProgress(progress);
    }
    
    // ===== GET TOP PLAYERS TESTS =====
    
    @Test
    public void testGetTopPlayersReturnsNonNull() {
        List<LeaderboardEntry> topPlayers = leaderboardService.getTopPlayers(10);
        
        assertNotNull(topPlayers);
    }
    
    @Test
    public void testGetTopPlayersReturnsEmptyInitially() {
        List<LeaderboardEntry> topPlayers = leaderboardService.getTopPlayers(10);
        
        assertTrue(topPlayers.isEmpty());
    }
    
    @Test
    public void testGetTopPlayersRespectsLimit() {
        // Create 10 users with progress
        for (int i = 1; i <= 10; i++) {
            createUserWithProgress("usr" + String.format("%02d", i), "User", String.valueOf(i), 100 - i);
        }
        
        List<LeaderboardEntry> topPlayers = leaderboardService.getTopPlayers(5);
        
        assertEquals(5, topPlayers.size());
    }
    
    @Test
    public void testGetTopPlayersReturnsSingleEntry() {
        createUserWithProgress("usr01", "User", "One", 100);
        
        List<LeaderboardEntry> topPlayers = leaderboardService.getTopPlayers(10);
        
        assertEquals(1, topPlayers.size());
    }
    
    @Test
    public void testGetTopPlayersReturnsMultipleEntries() {
        createUserWithProgress("usr02", "User", "One", 100);
        createUserWithProgress("usr03", "User", "Two", 95);
        createUserWithProgress("usr04", "User", "Three", 88);
        
        List<LeaderboardEntry> topPlayers = leaderboardService.getTopPlayers(10);
        
        assertEquals(3, topPlayers.size());
    }
    
    @Test
    public void testGetTopPlayersReturnsSortedByScore() {
        createUserWithProgress("usr05", "User", "Low", 70);
        createUserWithProgress("usr06", "User", "High", 100);
        createUserWithProgress("usr07", "User", "Mid", 85);
        
        List<LeaderboardEntry> topPlayers = leaderboardService.getTopPlayers(10);
        
        // First entry should have highest score
        assertTrue(topPlayers.get(0).getTotalScore() >= topPlayers.get(1).getTotalScore());
        assertTrue(topPlayers.get(1).getTotalScore() >= topPlayers.get(2).getTotalScore());
    }
    
    // ===== GET USER RANK TESTS =====
    
    @Test
    public void testGetUserRankReturnsNegativeForNonexistentUser() {
        int rank = leaderboardService.getUserRank("nonexistent");
        
        assertEquals(-1, rank);
    }
    
    @Test
    public void testGetUserRankReturnsOneForFirstPlace() {
        createUserWithProgress("usr08", "Top", "Player", 100);
        
        int rank = leaderboardService.getUserRank("usr08");
        
        assertEquals(1, rank);  // Rank is 1-based
    }
    
    @Test
    public void testGetUserRankReturnsCorrectRankForMultipleUsers() {
        createUserWithProgress("usr09", "User", "First", 100);
        createUserWithProgress("usr10", "User", "Second", 95);
        createUserWithProgress("usr11", "User", "Third", 88);
        
        int rank = leaderboardService.getUserRank("usr10");
        
        assertEquals(2, rank);  // Second place = rank 2
    }
    
    @Test
    public void testGetUserRankReturnsThirdPlace() {
        createUserWithProgress("usr12", "User", "First", 100);
        createUserWithProgress("usr13", "User", "Second", 95);
        createUserWithProgress("usr14", "User", "Third", 88);
        
        int rank = leaderboardService.getUserRank("usr14");
        
        assertEquals(3, rank);  // Third place = rank 3
    }
    
    @Test
    public void testGetUserRankUpdatesAfterNewScore() {
        createUserWithProgress("usr15", "User", "One", 90);
        
        int rankBefore = leaderboardService.getUserRank("usr15");
        
        // Add another user with higher score
        createUserWithProgress("usr16", "User", "Two", 100);
        
        int rankAfter = leaderboardService.getUserRank("usr15");
        
        assertTrue(rankAfter > rankBefore);  // Rank should decrease (number increases)
    }
    
    // ===== GET USER ENTRY TESTS =====
    
    @Test
    public void testGetUserEntryReturnsNullForNonexistentUser() {
        LeaderboardEntry entry = leaderboardService.getUserEntry("nonexistent");
        
        assertNull(entry);
    }
    
    @Test
    public void testGetUserEntryReturnsEntryAfterProgress() {
        createUserWithProgress("usr17", "Test", "User", 100);
        
        LeaderboardEntry entry = leaderboardService.getUserEntry("usr17");
        
        assertNotNull(entry);
    }
    
    @Test
    public void testGetUserEntryReturnsCorrectUserId() {
        createUserWithProgress("usr18", "Test", "User", 100);
        
        LeaderboardEntry entry = leaderboardService.getUserEntry("usr18");
        
        assertEquals("usr18", entry.getUserId());
    }
    
    @Test
    public void testGetUserEntryReturnsCorrectScore() {
        createUserWithProgress("usr19", "Test", "User", 95);
        
        LeaderboardEntry entry = leaderboardService.getUserEntry("usr19");
        
        assertEquals(95, entry.getTotalScore());
    }
    
    @Test
    public void testGetUserEntryReturnsCorrectPuzzleCount() {
        createUserWithProgress("usr20", "Test", "User", 100);
        
        LeaderboardEntry entry = leaderboardService.getUserEntry("usr20");
        
        assertEquals(1, entry.getPuzzlesCompleted());
    }
    
    // ===== GET FULL LEADERBOARD TESTS =====
    
    @Test
    public void testGetFullLeaderboardReturnsNonNull() {
        List<LeaderboardEntry> leaderboard = leaderboardService.getFullLeaderboard();
        
        assertNotNull(leaderboard);
    }
    
    @Test
    public void testGetFullLeaderboardReturnsEmptyInitially() {
        List<LeaderboardEntry> leaderboard = leaderboardService.getFullLeaderboard();
        
        assertTrue(leaderboard.isEmpty());
    }
    
    @Test
    public void testGetFullLeaderboardReturnsAllEntries() {
        createUserWithProgress("usr21", "User", "One", 100);
        createUserWithProgress("usr22", "User", "Two", 95);
        
        List<LeaderboardEntry> leaderboard = leaderboardService.getFullLeaderboard();
        
        assertEquals(2, leaderboard.size());
    }
    
    @Test
    public void testGetFullLeaderboardReturnsManyEntries() {
        // Create 20 users with progress
        for (int i = 1; i <= 20; i++) {
            createUserWithProgress("usr" + String.format("%02d", i + 22), "User", String.valueOf(i), 100 - i);
        }
        
        List<LeaderboardEntry> leaderboard = leaderboardService.getFullLeaderboard();
        
        assertEquals(20, leaderboard.size());
    }
    
    @Test
    public void testGetFullLeaderboardReturnsSortedEntries() {
        createUserWithProgress("usr43", "User", "Low", 60);
        createUserWithProgress("usr44", "User", "High", 100);
        createUserWithProgress("usr45", "User", "Mid", 80);
        
        List<LeaderboardEntry> leaderboard = leaderboardService.getFullLeaderboard();
        
        // Verify sorted by score descending
        for (int i = 0; i < leaderboard.size() - 1; i++) {
            assertTrue(leaderboard.get(i).getTotalScore() >= leaderboard.get(i + 1).getTotalScore());
        }
    }
}
