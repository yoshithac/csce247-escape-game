package com.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test cases for GameProgressService using TemporaryFolder
 * Each test runs in complete isolation with temporary files
 */
public class GameProgressServiceTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private GameProgressService progressService;
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
        progressService = new GameProgressService();
    }
    
    @After
    public void tearDown() {
        GameDataFacade.resetInstance();
        facade = null;
        progressService = null;
    }
    
    private void createTestData() throws IOException {
        String testUsers = "[]";
        Files.write(testUserFile.toPath(), testUsers.getBytes());
        
        String testGameData = "{" +
            "\"puzzles\":[" +
                "{\"puzzleId\":\"puzzle1\",\"title\":\"Puzzle 1\",\"difficulty\":\"EASY\",\"puzzleType\":\"MAZE\"}," +
                "{\"puzzleId\":\"puzzle2\",\"title\":\"Puzzle 2\",\"difficulty\":\"MEDIUM\",\"puzzleType\":\"WORD\"}," +
                "{\"puzzleId\":\"puzzle3\",\"title\":\"Puzzle 3\",\"difficulty\":\"HARD\",\"puzzleType\":\"CIPHER\"}" +
            "]," +
            "\"hints\":[]," +
            "\"userProgress\":[]," +
            "\"certificates\":[]," +
            "\"leaderboard\":[]" +
            "}";
        Files.write(testGameDataFile.toPath(), testGameData.getBytes());
    }
    
    // ===== GET USER PROGRESS TESTS =====
    
    @Test
    public void testGetUserProgressReturnsNonNull() {
        UserProgress progress = progressService.getUserProgress("usr01");
        
        assertNotNull(progress);
    }
    
    @Test
    public void testGetUserProgressCreatesNewForNewUser() {
        UserProgress progress = progressService.getUserProgress("usr02");
        
        assertEquals("usr02", progress.getUserId());
    }
    
    @Test
    public void testGetUserProgressReturnsSameInstanceForSameUser() {
        UserProgress progress1 = progressService.getUserProgress("usr03");
        UserProgress progress2 = progressService.getUserProgress("usr03");
        
        assertSame(progress1, progress2);
    }
    
    // ===== COMPLETE PUZZLE TESTS =====
    
    @Test
    public void testCompletePuzzleMarksAsCompleted() {
        progressService.completePuzzle("usr04", "puzzle1", 100);
        
        boolean completed = progressService.isPuzzleCompleted("usr04", "puzzle1");
        
        assertTrue(completed);
    }
    
    @Test
    public void testCompletePuzzleDoesNotAffectOtherPuzzles() {
        progressService.completePuzzle("usr05", "puzzle1", 100);
        
        boolean completed = progressService.isPuzzleCompleted("usr05", "puzzle2");
        
        assertFalse(completed);
    }
    
    @Test
    public void testCompletePuzzleStoresScore() {
        progressService.completePuzzle("usr06", "puzzle1", 95);
        UserProgress progress = progressService.getUserProgress("usr06");
        
        assertEquals(Integer.valueOf(95), progress.getPuzzleScores().get("puzzle1"));
    }
    
    // ===== IS PUZZLE COMPLETED TESTS =====
    
    @Test
    public void testIsPuzzleCompletedReturnsFalseForNewUser() {
        boolean completed = progressService.isPuzzleCompleted("usr07", "puzzle1");
        
        assertFalse(completed);
    }
    
    @Test
    public void testIsPuzzleCompletedReturnsTrueAfterCompletion() {
        progressService.completePuzzle("usr08", "puzzle1", 100);
        
        boolean completed = progressService.isPuzzleCompleted("usr08", "puzzle1");
        
        assertTrue(completed);
    }
    
    // ===== GET AVAILABLE PUZZLES TESTS =====
    
    @Test
    public void testGetAvailablePuzzlesReturnsNonNull() {
        List<Puzzle> puzzles = progressService.getAvailablePuzzles("usr09");
        
        assertNotNull(puzzles);
    }
    
    @Test
    public void testGetAvailablePuzzlesReturnsAllForNewUser() {
        List<Puzzle> puzzles = progressService.getAvailablePuzzles("usr10");
        
        assertEquals(3, puzzles.size());
    }
    
    @Test
    public void testGetAvailablePuzzlesExcludesCompleted() {
        progressService.completePuzzle("usr11", "puzzle1", 100);
        
        List<Puzzle> puzzles = progressService.getAvailablePuzzles("usr11");
        
        assertEquals(2, puzzles.size());
    }
    
    @Test
    public void testGetAvailablePuzzlesReturnsEmptyWhenAllCompleted() {
        progressService.completePuzzle("usr12", "puzzle1", 100);
        progressService.completePuzzle("usr12", "puzzle2", 95);
        progressService.completePuzzle("usr12", "puzzle3", 88);
        
        List<Puzzle> puzzles = progressService.getAvailablePuzzles("usr12");
        
        assertTrue(puzzles.isEmpty());
    }
    
    // ===== GET COMPLETED PUZZLES TESTS =====
    
    @Test
    public void testGetCompletedPuzzlesReturnsNonNull() {
        List<Puzzle> puzzles = progressService.getCompletedPuzzles("usr13");
        
        assertNotNull(puzzles);
    }
    
    @Test
    public void testGetCompletedPuzzlesReturnsEmptyForNewUser() {
        List<Puzzle> puzzles = progressService.getCompletedPuzzles("usr14");
        
        assertTrue(puzzles.isEmpty());
    }
    
    @Test
    public void testGetCompletedPuzzlesReturnsCompleted() {
        progressService.completePuzzle("usr15", "puzzle1", 100);
        
        List<Puzzle> puzzles = progressService.getCompletedPuzzles("usr15");
        
        assertEquals(1, puzzles.size());
    }
    
    @Test
    public void testGetCompletedPuzzlesReturnsAllCompleted() {
        progressService.completePuzzle("usr16", "puzzle1", 100);
        progressService.completePuzzle("usr16", "puzzle2", 95);
        
        List<Puzzle> puzzles = progressService.getCompletedPuzzles("usr16");
        
        assertEquals(2, puzzles.size());
    }
    
    // ===== GET PROGRESS STATS TESTS =====
    
    @Test
    public void testGetProgressStatsReturnsNonNull() {
        Map<String, Integer> stats = progressService.getProgressStats("usr17");
        
        assertNotNull(stats);
    }
    
    @Test
    public void testGetProgressStatsContainsTotalPuzzles() {
        Map<String, Integer> stats = progressService.getProgressStats("usr18");
        
        assertEquals(Integer.valueOf(3), stats.get("totalPuzzles"));
    }
    
    @Test
    public void testGetProgressStatsContainsCompleted() {
        progressService.completePuzzle("usr19", "puzzle1", 100);
        
        Map<String, Integer> stats = progressService.getProgressStats("usr19");
        
        assertEquals(Integer.valueOf(1), stats.get("completed"));
    }
    
    @Test
    public void testGetProgressStatsContainsRemaining() {
        progressService.completePuzzle("usr20", "puzzle1", 100);
        
        Map<String, Integer> stats = progressService.getProgressStats("usr20");
        
        assertEquals(Integer.valueOf(2), stats.get("remaining"));
    }
    
    @Test
    public void testGetProgressStatsContainsTotalScore() {
        progressService.completePuzzle("usr21", "puzzle1", 100);
        progressService.completePuzzle("usr21", "puzzle2", 95);
        
        Map<String, Integer> stats = progressService.getProgressStats("usr21");
        
        assertEquals(Integer.valueOf(195), stats.get("totalScore"));
    }
    
    @Test
    public void testGetProgressStatsCalculatesCompletionPercentage() {
        progressService.completePuzzle("usr22", "puzzle1", 100);
        
        Map<String, Integer> stats = progressService.getProgressStats("usr22");
        
        assertEquals(Integer.valueOf(33), stats.get("completionPercentage"));
    }
    
    @Test
    public void testGetProgressStatsCalculatesFullCompletion() {
        progressService.completePuzzle("usr23", "puzzle1", 100);
        progressService.completePuzzle("usr23", "puzzle2", 95);
        progressService.completePuzzle("usr23", "puzzle3", 88);
        
        Map<String, Integer> stats = progressService.getProgressStats("usr23");
        
        assertEquals(Integer.valueOf(100), stats.get("completionPercentage"));
    }
}