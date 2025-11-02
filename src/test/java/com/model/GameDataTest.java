package com.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for GameData entity
 * Each test method contains exactly one assertion
 */
public class GameDataTest {
    
    private GameData gameData;
    
    @Before
    public void setUp() {
        gameData = new GameData();
    }
    
    @Test
    public void testDefaultConstructorInitializesPuzzlesList() {
        assertNotNull(gameData.getPuzzles());
    }
    
    @Test
    public void testDefaultConstructorInitializesHintsList() {
        assertNotNull(gameData.getHints());
    }
    
    @Test
    public void testDefaultConstructorInitializesUserProgressList() {
        assertNotNull(gameData.getUserProgress());
    }
    
    @Test
    public void testDefaultConstructorInitializesCertificatesList() {
        assertNotNull(gameData.getCertificates());
    }
    
    @Test
    public void testDefaultConstructorInitializesLeaderboardList() {
        assertNotNull(gameData.getLeaderboard());
    }
    
    @Test
    public void testDefaultConstructorCreatesEmptyPuzzlesList() {
        assertTrue(gameData.getPuzzles().isEmpty());
    }
    
    @Test
    public void testDefaultConstructorCreatesEmptyHintsList() {
        assertTrue(gameData.getHints().isEmpty());
    }
    
    @Test
    public void testDefaultConstructorCreatesEmptyUserProgressList() {
        assertTrue(gameData.getUserProgress().isEmpty());
    }
    
    @Test
    public void testDefaultConstructorCreatesEmptyCertificatesList() {
        assertTrue(gameData.getCertificates().isEmpty());
    }
    
    @Test
    public void testDefaultConstructorCreatesEmptyLeaderboardList() {
        assertTrue(gameData.getLeaderboard().isEmpty());
    }
    
    @Test
    public void testParameterizedConstructorSetsPuzzles() {
        List<Puzzle> puzzles = new ArrayList<>();
        puzzles.add(new Puzzle());
        GameData data = new GameData(puzzles, null, null, null, null);
        assertEquals(puzzles, data.getPuzzles());
    }
    
    @Test
    public void testParameterizedConstructorSetsHints() {
        List<Hint> hints = new ArrayList<>();
        hints.add(new Hint());
        GameData data = new GameData(null, hints, null, null, null);
        assertEquals(hints, data.getHints());
    }
    
    @Test
    public void testParameterizedConstructorSetsUserProgress() {
        List<UserProgress> progress = new ArrayList<>();
        progress.add(new UserProgress());
        GameData data = new GameData(null, null, progress, null, null);
        assertEquals(progress, data.getUserProgress());
    }
    
    @Test
    public void testParameterizedConstructorSetsCertificates() {
        List<Certificate> certs = new ArrayList<>();
        certs.add(new Certificate());
        GameData data = new GameData(null, null, null, certs, null);
        assertEquals(certs, data.getCertificates());
    }
    
    @Test
    public void testParameterizedConstructorSetsLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        leaderboard.add(new LeaderboardEntry());
        GameData data = new GameData(null, null, null, null, leaderboard);
        assertEquals(leaderboard, data.getLeaderboard());
    }
    
    @Test
    public void testParameterizedConstructorHandlesNullPuzzles() {
        GameData data = new GameData(null, null, null, null, null);
        assertNotNull(data.getPuzzles());
    }
    
    @Test
    public void testParameterizedConstructorHandlesNullHints() {
        GameData data = new GameData(null, null, null, null, null);
        assertNotNull(data.getHints());
    }
    
    @Test
    public void testParameterizedConstructorHandlesNullUserProgress() {
        GameData data = new GameData(null, null, null, null, null);
        assertNotNull(data.getUserProgress());
    }
    
    @Test
    public void testParameterizedConstructorHandlesNullCertificates() {
        GameData data = new GameData(null, null, null, null, null);
        assertNotNull(data.getCertificates());
    }
    
    @Test
    public void testParameterizedConstructorHandlesNullLeaderboard() {
        GameData data = new GameData(null, null, null, null, null);
        assertNotNull(data.getLeaderboard());
    }
    
    @Test
    public void testSetPuzzles() {
        List<Puzzle> puzzles = new ArrayList<>();
        puzzles.add(new Puzzle());
        gameData.setPuzzles(puzzles);
        assertEquals(puzzles, gameData.getPuzzles());
    }
    
    @Test
    public void testSetHints() {
        List<Hint> hints = new ArrayList<>();
        hints.add(new Hint());
        gameData.setHints(hints);
        assertEquals(hints, gameData.getHints());
    }
    
    @Test
    public void testSetUserProgress() {
        List<UserProgress> progress = new ArrayList<>();
        progress.add(new UserProgress());
        gameData.setUserProgress(progress);
        assertEquals(progress, gameData.getUserProgress());
    }
    
    @Test
    public void testSetCertificates() {
        List<Certificate> certs = new ArrayList<>();
        certs.add(new Certificate());
        gameData.setCertificates(certs);
        assertEquals(certs, gameData.getCertificates());
    }
    
    @Test
    public void testSetLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        leaderboard.add(new LeaderboardEntry());
        gameData.setLeaderboard(leaderboard);
        assertEquals(leaderboard, gameData.getLeaderboard());
    }
}
