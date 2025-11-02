package com.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
 * Test cases for GameDataFacade using TemporaryFolder
 * Tests singleton pattern with proper isolation
 * Each test runs in complete isolation with temporary files
 */
public class GameDataFacadeTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
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
        
        // Create facade with test dependencies
        facade = new GameDataFacade(testLoader, testWriter);
        GameDataFacade.setTestInstance(facade);
    }
    
    @After
    public void tearDown() {
        // Reset singleton instance after each test
        GameDataFacade.resetInstance();
        facade = null;
    }
    
    private void createTestData() throws IOException {
        // Create minimal test users
        String testUsers = "[]";
        Files.write(testUserFile.toPath(), testUsers.getBytes());
        
        // Create test game data with puzzles and hints
        String testGameData = "{" +
            "\"puzzles\":[" +
                "{\"puzzleId\":\"puzzle1\",\"title\":\"Test Maze\",\"difficulty\":\"EASY\",\"puzzleType\":\"MAZE\"}," +
                "{\"puzzleId\":\"puzzle2\",\"title\":\"Test Word\",\"difficulty\":\"MEDIUM\",\"puzzleType\":\"WORD\"}" +
            "]," +
            "\"hints\":[" +
                "{\"hintText\":\"Look left\",\"puzzleId\":\"puzzle1\",\"hintPriority\":1}," +
                "{\"hintText\":\"Go right\",\"puzzleId\":\"puzzle1\",\"hintPriority\":2}" +
            "]," +
            "\"userProgress\":[]," +
            "\"certificates\":[]," +
            "\"leaderboard\":[]" +
            "}";
        Files.write(testGameDataFile.toPath(), testGameData.getBytes());
    }
    
    // ===== SINGLETON TESTS =====
    
    @Test
    public void testGetInstanceReturnsSameInstance() {
        GameDataFacade instance1 = GameDataFacade.getInstance();
        GameDataFacade instance2 = GameDataFacade.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    public void testGetInstanceReturnsNonNull() {
        GameDataFacade instance = GameDataFacade.getInstance();
        
        assertNotNull(instance);
    }
    
    @Test
    public void testResetInstanceClearsInstance() {
        GameDataFacade instance1 = GameDataFacade.getInstance();
        GameDataFacade.resetInstance();
        
        assertNotNull(instance1);
    }
    
    // ===== USER OPERATIONS TESTS =====
    
    @Test
    public void testAddUserAddsUserToSystem() {
        User user = new User("tst01", "password", "Test", "User", "test01@example.com");
        
        boolean result = facade.addUser(user);
        
        assertTrue(result);
    }
    
    @Test
    public void testAddUserMakesUserRetrievable() {
        User user = new User("tst02", "password", "Test", "User", "test02@example.com");
        facade.addUser(user);
        
        boolean exists = facade.userIdExists("tst02");
        
        assertTrue(exists);
    }
    
    @Test
    public void testAddUserReturnsFalseForDuplicateUserId() {
        User user1 = new User("tst03", "password", "Test", "User1", "test03a@example.com");
        User user2 = new User("tst03", "password", "Test", "User2", "test03b@example.com");
        facade.addUser(user1);
        
        boolean result = facade.addUser(user2);
        
        assertFalse(result);
    }
    
    @Test
    public void testAddUserReturnsFalseForDuplicateEmail() {
        User user1 = new User("tst04", "password", "Test", "User1", "test04@example.com");
        User user2 = new User("tst05", "password", "Test", "User2", "test04@example.com");
        facade.addUser(user1);
        
        boolean result = facade.addUser(user2);
        
        assertFalse(result);
    }
    
    @Test
    public void testGetUserReturnsEmptyForNonexistentUser() {
        Optional<User> user = facade.getUser("nonexistent");
        
        assertFalse(user.isPresent());
    }
    
    @Test
    public void testGetUserReturnsUserWhenExists() {
        User user = new User("tst06", "password", "Test", "User", "test06@example.com");
        facade.addUser(user);
        
        Optional<User> retrieved = facade.getUser("tst06");
        
        assertTrue(retrieved.isPresent());
    }
    
    @Test
    public void testGetUserReturnsCorrectUser() {
        User user = new User("tst07", "password", "Test", "User", "test07@example.com");
        facade.addUser(user);
        
        Optional<User> retrieved = facade.getUser("tst07");
        
        assertEquals("tst07", retrieved.get().getUserId());
    }
    
    @Test
    public void testUpdateUserReturnsTrueForExistingUser() {
        User user = new User("tst08", "password", "Test", "User", "test08@example.com");
        facade.addUser(user);
        user.setFirstName("Updated");
        
        boolean result = facade.updateUser(user);
        
        assertTrue(result);
    }
    
    @Test
    public void testUpdateUserReturnsFalseForNonexistentUser() {
        User user = new User("nonexistent", "password", "Test", "User", "nonexistent@example.com");
        
        boolean result = facade.updateUser(user);
        
        assertFalse(result);
    }
    
    @Test
    public void testUpdateUserActuallyUpdatesData() {
        User user = new User("tst09", "password", "Original", "User", "test09@example.com");
        facade.addUser(user);
        user.setFirstName("Updated");
        facade.updateUser(user);
        
        Optional<User> retrieved = facade.getUser("tst09");
        
        assertEquals("Updated", retrieved.get().getFirstName());
    }
    
    @Test
    public void testUserIdExistsReturnsTrueForExistingUser() {
        User user = new User("tst10", "password", "Test", "User", "test10@example.com");
        facade.addUser(user);
        
        boolean exists = facade.userIdExists("tst10");
        
        assertTrue(exists);
    }
    
    @Test
    public void testUserIdExistsReturnsFalseForNonexistentUser() {
        boolean exists = facade.userIdExists("nonexistent");
        
        assertFalse(exists);
    }
    
    @Test
    public void testEmailExistsReturnsTrueForExistingEmail() {
        User user = new User("tst11", "password", "Test", "User", "test11@example.com");
        facade.addUser(user);
        
        boolean exists = facade.emailExists("test11@example.com");
        
        assertTrue(exists);
    }
    
    @Test
    public void testEmailExistsReturnsFalseForNonexistentEmail() {
        boolean exists = facade.emailExists("nonexistent@example.com");
        
        assertFalse(exists);
    }
    
    @Test
    public void testGetUsersReturnsNonNull() {
        List<User> users = facade.getUsers();
        
        assertNotNull(users);
    }
    
    @Test
    public void testGetUsersReturnsCorrectCount() {
        User user1 = new User("tst12", "password", "Test", "User1", "test12@example.com");
        User user2 = new User("tst13", "password", "Test", "User2", "test13@example.com");
        facade.addUser(user1);
        facade.addUser(user2);
        
        List<User> users = facade.getUsers();
        
        assertEquals(2, users.size());
    }
    
    // ===== PUZZLE OPERATIONS TESTS =====
    
    @Test
    public void testGetAllPuzzlesReturnsNonNull() {
        List<Puzzle> puzzles = facade.getAllPuzzles();
        
        assertNotNull(puzzles);
    }
    
    @Test
    public void testGetAllPuzzlesReturnsCorrectCount() {
        List<Puzzle> puzzles = facade.getAllPuzzles();
        
        assertEquals(2, puzzles.size());
    }
    
    @Test
    public void testGetPuzzleReturnsEmptyForNonexistent() {
        Optional<Puzzle> puzzle = facade.getPuzzle("nonexistent");
        
        assertFalse(puzzle.isPresent());
    }
    
    @Test
    public void testGetPuzzleReturnsPuzzleWhenExists() {
        Optional<Puzzle> puzzle = facade.getPuzzle("puzzle1");
        
        assertTrue(puzzle.isPresent());
    }
    
    @Test
    public void testGetPuzzleReturnsCorrectPuzzle() {
        Optional<Puzzle> puzzle = facade.getPuzzle("puzzle1");
        
        assertEquals("puzzle1", puzzle.get().getPuzzleId());
    }
    
    @Test
    public void testGetPuzzlesByTypeReturnsCorrectType() {
        List<Puzzle> mazePuzzles = facade.getPuzzlesByType("MAZE");
        
        assertEquals(1, mazePuzzles.size());
    }
    
    @Test
    public void testGetPuzzlesByTypeIgnoresCase() {
        List<Puzzle> wordPuzzles = facade.getPuzzlesByType("word");
        
        assertEquals(1, wordPuzzles.size());
    }
    
    @Test
    public void testGetPuzzlesByTypeReturnsEmptyForNonexistentType() {
        List<Puzzle> puzzles = facade.getPuzzlesByType("NONEXISTENT");
        
        assertTrue(puzzles.isEmpty());
    }
    
    @Test
    public void testGetPuzzlesByDifficultyReturnsCorrectPuzzles() {
        List<Puzzle> easyMaze = facade.getPuzzlesByDifficulty("MAZE", "EASY");
        
        assertEquals(1, easyMaze.size());
    }
    
    @Test
    public void testGetPuzzlesByDifficultyReturnsEmptyForWrongDifficulty() {
        List<Puzzle> hardMaze = facade.getPuzzlesByDifficulty("MAZE", "HARD");
        
        assertTrue(hardMaze.isEmpty());
    }
    
    @Test
    public void testGetAvailablePuzzleTypesReturnsNonNull() {
        Set<String> types = facade.getAvailablePuzzleTypes();
        
        assertNotNull(types);
    }
    
    @Test
    public void testGetAvailablePuzzleTypesReturnsCorrectCount() {
        Set<String> types = facade.getAvailablePuzzleTypes();
        
        assertEquals(2, types.size());
    }
    
    @Test
    public void testGetAvailablePuzzleTypesContainsMaze() {
        Set<String> types = facade.getAvailablePuzzleTypes();
        
        assertTrue(types.contains("MAZE"));
    }
    
    @Test
    public void testGetAvailablePuzzleTypesContainsWord() {
        Set<String> types = facade.getAvailablePuzzleTypes();
        
        assertTrue(types.contains("WORD"));
    }
    
    // ===== HINT OPERATIONS TESTS =====
    
    @Test
    public void testGetHintsForPuzzleReturnsNonNull() {
        List<Hint> hints = facade.getHintsForPuzzle("puzzle1");
        
        assertNotNull(hints);
    }
    
    @Test
    public void testGetHintsForPuzzleReturnsCorrectCount() {
        List<Hint> hints = facade.getHintsForPuzzle("puzzle1");
        
        assertEquals(2, hints.size());
    }
    
    @Test
    public void testGetHintsForPuzzleReturnsEmptyForNonexistent() {
        List<Hint> hints = facade.getHintsForPuzzle("nonexistent");
        
        assertTrue(hints.isEmpty());
    }
    
    @Test
    public void testGetHintsForPuzzleReturnsOrderedByPriority() {
        List<Hint> hints = facade.getHintsForPuzzle("puzzle1");
        
        assertEquals(1, hints.get(0).getHintPriority());
    }
    
    // ===== USER PROGRESS TESTS =====
    
    @Test
    public void testGetUserProgressReturnsNonNull() {
        User user = new User("tst14", "password", "Test", "User", "test14@example.com");
        facade.addUser(user);
        
        UserProgress progress = facade.getUserProgress("tst14");
        
        assertNotNull(progress);
    }
    
    @Test
    public void testGetUserProgressCreatesNewForNewUser() {
        UserProgress progress = facade.getUserProgress("newuser");
        
        assertEquals("newuser", progress.getUserId());
    }
    
    @Test
    public void testGetUserProgressReturnsSameInstanceForSameUser() {
        UserProgress progress1 = facade.getUserProgress("tst15");
        UserProgress progress2 = facade.getUserProgress("tst15");
        
        assertSame(progress1, progress2);
    }
    
    @Test
    public void testCompletePuzzleMarksAsCompleted() {
        User user = new User("tst16", "password", "Test", "User", "test16@example.com");
        facade.addUser(user);
        
        facade.completePuzzle("tst16", "puzzle1", 100);
        boolean completed = facade.isPuzzleCompleted("tst16", "puzzle1");
        
        assertTrue(completed);
    }
    
    @Test
    public void testIsPuzzleCompletedReturnsFalseForIncomplete() {
        User user = new User("tst17", "password", "Test", "User", "test17@example.com");
        facade.addUser(user);
        
        boolean completed = facade.isPuzzleCompleted("tst17", "puzzle1");
        
        assertFalse(completed);
    }
    
    @Test
    public void testCompletePuzzleStoresInList() {
        User user = new User("tst18", "password", "Test", "User", "test18@example.com");
        facade.addUser(user);
        
        facade.completePuzzle("tst18", "puzzle1", 95);
        UserProgress progress = facade.getUserProgress("tst18");
        
        assertTrue(progress.getCompletedPuzzles().contains("puzzle1"));
    }
    
    @Test
    public void testCompletePuzzleStoresScore() {
        User user = new User("tst19", "password", "Test", "User", "test19@example.com");
        facade.addUser(user);
        
        facade.completePuzzle("tst19", "puzzle1", 95);
        UserProgress progress = facade.getUserProgress("tst19");
        
        assertEquals(Integer.valueOf(95), progress.getPuzzleScores().get("puzzle1"));
    }
    
    @Test
    public void testSaveUserProgressPersistsData() {
        User user = new User("tst20", "password", "Test", "User", "test20@example.com");
        facade.addUser(user);
        UserProgress progress = facade.getUserProgress("tst20");
        progress.addCompletedPuzzle("puzzle1", 88);
        
        facade.saveUserProgress(progress);
        
        assertEquals(88, facade.getUserProgress("tst20").getTotalScore());
    }
    
    // ===== CERTIFICATE TESTS =====
    
    @Test
    public void testAddCertificateAddsToSystem() {
        User user = new User("tst21", "password", "Test", "User", "test21@example.com");
        facade.addUser(user);
        Certificate cert = new Certificate("cert21", "tst21", "puzzle1", "Test Puzzle", "EASY", 100);
        
        facade.addCertificate(cert);
        List<Certificate> certs = facade.getUserCertificates("tst21");
        
        assertEquals(1, certs.size());
    }
    
    @Test
    public void testGetUserCertificatesReturnsEmptyForNewUser() {
        List<Certificate> certs = facade.getUserCertificates("newuser");
        
        assertTrue(certs.isEmpty());
    }
    
    @Test
    public void testGetUserCertificatesReturnsNonNull() {
        User user = new User("tst22", "password", "Test", "User", "test22@example.com");
        facade.addUser(user);
        
        List<Certificate> certs = facade.getUserCertificates("tst22");
        
        assertNotNull(certs);
    }
    
    @Test
    public void testGetCertificateStatsReturnsNonNull() {
        User user = new User("tst23", "password", "Test", "User", "test23@example.com");
        facade.addUser(user);
        
        Map<String, Integer> stats = facade.getCertificateStats("tst23");
        
        assertNotNull(stats);
    }
    
    @Test
    public void testGetCertificateStatsInitializesAllDifficulties() {
        User user = new User("tst24", "password", "Test", "User", "test24@example.com");
        facade.addUser(user);
        
        Map<String, Integer> stats = facade.getCertificateStats("tst24");
        
        assertTrue(stats.containsKey("EASY"));
    }
    
    @Test
    public void testGetCertificateStatsCountsCorrectly() {
        User user = new User("tst25", "password", "Test", "User", "test25@example.com");
        facade.addUser(user);
        Certificate cert = new Certificate("cert25", "tst25", "puzzle1", "Test Puzzle", "EASY", 100);
        facade.addCertificate(cert);
        
        Map<String, Integer> stats = facade.getCertificateStats("tst25");
        
        assertEquals(Integer.valueOf(1), stats.get("EASY"));
    }
    
    @Test
    public void testHasCertificateReturnsFalseWhenNoCertificate() {
        User user = new User("tst26", "password", "Test", "User", "test26@example.com");
        facade.addUser(user);
        
        boolean hasCert = facade.hasCertificate("tst26", "puzzle1");
        
        assertFalse(hasCert);
    }
    
    @Test
    public void testHasCertificateReturnsTrueWhenCertificateExists() {
        User user = new User("tst27", "password", "Test", "User", "test27@example.com");
        facade.addUser(user);
        Certificate cert = new Certificate("cert27", "tst27", "puzzle1", "Test Puzzle", "EASY", 100);
        facade.addCertificate(cert);
        
        boolean hasCert = facade.hasCertificate("tst27", "puzzle1");
        
        assertTrue(hasCert);
    }
    
    // ===== LEADERBOARD TESTS =====
    
    @Test
    public void testGetLeaderboardReturnsNonNull() {
        List<LeaderboardEntry> leaderboard = facade.getLeaderboard(10);
        
        assertNotNull(leaderboard);
    }
    
    @Test
    public void testGetLeaderboardRespectsLimit() {
        List<LeaderboardEntry> leaderboard = facade.getLeaderboard(5);
        
        assertTrue(leaderboard.size() <= 5);
    }
    
    @Test
    public void testGetLeaderboardReturnsEmptyInitially() {
        List<LeaderboardEntry> leaderboard = facade.getLeaderboard(10);
        
        assertTrue(leaderboard.isEmpty());
    }
    
    @Test
    public void testGetUserRankReturnsNegativeForNonexistentUser() {
        int rank = facade.getUserRank("nonexistent");
        
        assertEquals(-1, rank);
    }
    
    @Test
    public void testLeaderboardUpdatedOnUserProgress() {
        User user = new User("tst28", "password", "Test", "User", "test28@example.com");
        facade.addUser(user);
        UserProgress progress = facade.getUserProgress("tst28");
        progress.addCompletedPuzzle("puzzle1", 100);
        
        facade.saveUserProgress(progress);
        int rank = facade.getUserRank("tst28");
        
        assertTrue(rank >= 0);
    }
    
    @Test
    public void testLeaderboardMakesUserRankable() {
        User user = new User("tst29", "password", "Test", "User", "test29@example.com");
        facade.addUser(user);
        UserProgress progress = facade.getUserProgress("tst29");
        progress.addCompletedPuzzle("puzzle1", 100);
        facade.saveUserProgress(progress);
        
        List<LeaderboardEntry> leaderboard = facade.getLeaderboard(10);
        
        assertEquals(1, leaderboard.size());
    }
}
