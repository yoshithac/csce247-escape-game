package com.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test cases for CertificateService using TemporaryFolder
 * Each test runs in complete isolation with temporary files
 */
public class CertificateServiceTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private CertificateService certificateService;
    private GameDataFacade facade;
    private File testUserFile;
    private File testGameDataFile;
    private Puzzle testPuzzle;
    
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
        certificateService = new CertificateService();
        
        // Create test puzzle
        testPuzzle = new Puzzle();
        testPuzzle.setPuzzleId("puzzle1");
        testPuzzle.setTitle("Test Puzzle");
        testPuzzle.setDifficulty("EASY");
        testPuzzle.setPuzzleType("MAZE");
    }
    
    @After
    public void tearDown() {
        GameDataFacade.resetInstance();
        facade = null;
        certificateService = null;
    }
    
    private void createTestData() throws IOException {
        String testUsers = "[]";
        Files.write(testUserFile.toPath(), testUsers.getBytes());
        
        String testGameData = "{" +
            "\"puzzles\":[" +
                "{\"puzzleId\":\"puzzle1\",\"title\":\"Test Puzzle\",\"difficulty\":\"EASY\",\"puzzleType\":\"MAZE\"}" +
            "]," +
            "\"hints\":[]," +
            "\"userProgress\":[]," +
            "\"certificates\":[]," +
            "\"leaderboard\":[]" +
            "}";
        Files.write(testGameDataFile.toPath(), testGameData.getBytes());
    }
    
    // ===== AWARD CERTIFICATE TESTS =====
    
    @Test
    public void testAwardCertificateReturnsNonNull() {
        Certificate cert = certificateService.awardCertificate("usr01", testPuzzle, 100);
        
        assertNotNull(cert);
    }
    
    @Test
    public void testAwardCertificateHasCorrectUserId() {
        Certificate cert = certificateService.awardCertificate("usr02", testPuzzle, 95);
        
        assertEquals("usr02", cert.getUserId());
    }
    
    @Test
    public void testAwardCertificateHasCorrectPuzzleId() {
        Certificate cert = certificateService.awardCertificate("usr03", testPuzzle, 88);
        
        assertEquals("puzzle1", cert.getPuzzleId());
    }
    
    @Test
    public void testAwardCertificateHasCorrectScore() {
        Certificate cert = certificateService.awardCertificate("usr04", testPuzzle, 92);
        
        assertEquals(92, cert.getScoreAchieved());
    }
    
    @Test
    public void testAwardCertificateHasCorrectDifficulty() {
        Certificate cert = certificateService.awardCertificate("usr05", testPuzzle, 100);
        
        assertEquals("EASY", cert.getDifficulty());
    }
    
    @Test
    public void testAwardCertificateGeneratesUniqueId() {
        Certificate cert1 = certificateService.awardCertificate("usr06", testPuzzle, 100);
        Certificate cert2 = certificateService.awardCertificate("usr06", testPuzzle, 100);
        
        assertNotEquals(cert1.getCertificateId(), cert2.getCertificateId());
    }
    
    @Test
    public void testAwardCertificateStoresInSystem() {
        certificateService.awardCertificate("usr07", testPuzzle, 100);
        
        assertTrue(certificateService.hasCertificate("usr07", "puzzle1"));
    }
    
    // ===== GET USER CERTIFICATES TESTS =====
    
    @Test
    public void testGetUserCertificatesReturnsNonNull() {
        List<Certificate> certs = certificateService.getUserCertificates("usr08");
        
        assertNotNull(certs);
    }
    
    @Test
    public void testGetUserCertificatesReturnsEmptyForNewUser() {
        List<Certificate> certs = certificateService.getUserCertificates("usr09");
        
        assertTrue(certs.isEmpty());
    }
    
    @Test
    public void testGetUserCertificatesReturnsAwardedCertificates() {
        certificateService.awardCertificate("usr10", testPuzzle, 100);
        
        List<Certificate> certs = certificateService.getUserCertificates("usr10");
        
        assertEquals(1, certs.size());
    }
    
    @Test
    public void testGetUserCertificatesReturnsMultipleCertificates() {
        Puzzle puzzle2 = new Puzzle();
        puzzle2.setPuzzleId("puzzle2");
        puzzle2.setTitle("Test Puzzle 2");
        puzzle2.setDifficulty("MEDIUM");
        
        certificateService.awardCertificate("usr11", testPuzzle, 100);
        certificateService.awardCertificate("usr11", puzzle2, 95);
        
        List<Certificate> certs = certificateService.getUserCertificates("usr11");
        
        assertEquals(2, certs.size());
    }
    
    // ===== CERTIFICATE STATS TESTS =====
    
    @Test
    public void testGetCertificateStatsReturnsNonNull() {
        Map<String, Integer> stats = certificateService.getCertificateStats("usr12");
        
        assertNotNull(stats);
    }
    
    @Test
    public void testGetCertificateStatsInitializesAllDifficulties() {
        Map<String, Integer> stats = certificateService.getCertificateStats("usr13");
        
        assertTrue(stats.containsKey("EASY"));
        assertTrue(stats.containsKey("MEDIUM"));
        assertTrue(stats.containsKey("HARD"));
    }
    
    @Test
    public void testGetCertificateStatsCountsCorrectly() {
        certificateService.awardCertificate("usr14", testPuzzle, 100);
        
        Map<String, Integer> stats = certificateService.getCertificateStats("usr14");
        
        assertEquals(Integer.valueOf(1), stats.get("EASY"));
    }
    
    @Test
    public void testGetCertificateStatsCountsMultipleDifficulties() {
        Puzzle mediumPuzzle = new Puzzle();
        mediumPuzzle.setPuzzleId("puzzle2");
        mediumPuzzle.setTitle("Medium Puzzle");
        mediumPuzzle.setDifficulty("MEDIUM");
        
        certificateService.awardCertificate("usr15", testPuzzle, 100);
        certificateService.awardCertificate("usr15", mediumPuzzle, 90);
        
        Map<String, Integer> stats = certificateService.getCertificateStats("usr15");
        
        assertEquals(Integer.valueOf(1), stats.get("EASY"));
        assertEquals(Integer.valueOf(1), stats.get("MEDIUM"));
    }
    
    // ===== HAS CERTIFICATE TESTS =====
    
    @Test
    public void testHasCertificateReturnsFalseForNoCertificate() {
        boolean hasCert = certificateService.hasCertificate("usr16", "puzzle1");
        
        assertFalse(hasCert);
    }
    
    @Test
    public void testHasCertificateReturnsTrueAfterAwarding() {
        certificateService.awardCertificate("usr17", testPuzzle, 100);
        
        boolean hasCert = certificateService.hasCertificate("usr17", "puzzle1");
        
        assertTrue(hasCert);
    }
    
    @Test
    public void testHasCertificateReturnsFalseForDifferentPuzzle() {
        certificateService.awardCertificate("usr18", testPuzzle, 100);
        
        boolean hasCert = certificateService.hasCertificate("usr18", "puzzle2");
        
        assertFalse(hasCert);
    }
    
    // ===== GET CERTIFICATE COUNT TESTS =====
    
    @Test
    public void testGetCertificateCountReturnsZeroForNewUser() {
        int count = certificateService.getCertificateCount("usr19");
        
        assertEquals(0, count);
    }
    
    @Test
    public void testGetCertificateCountReturnsCorrectCount() {
        certificateService.awardCertificate("usr20", testPuzzle, 100);
        
        int count = certificateService.getCertificateCount("usr20");
        
        assertEquals(1, count);
    }
    
    @Test
    public void testGetCertificateCountReturnsCorrectCountForMultiple() {
        Puzzle puzzle2 = new Puzzle();
        puzzle2.setPuzzleId("puzzle2");
        puzzle2.setTitle("Puzzle 2");
        puzzle2.setDifficulty("MEDIUM");
        
        certificateService.awardCertificate("usr21", testPuzzle, 100);
        certificateService.awardCertificate("usr21", puzzle2, 95);
        
        int count = certificateService.getCertificateCount("usr21");
        
        assertEquals(2, count);
    }
}
