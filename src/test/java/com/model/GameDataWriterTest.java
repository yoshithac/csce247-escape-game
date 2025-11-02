package com.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test cases for GameDataWriter using TemporaryFolder
 * Each test runs in complete isolation with temporary files
 */
public class GameDataWriterTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private GameDataWriter writer;
    private File testUserFile;
    private File testGameDataFile;
    
    @Before
    public void setUp() throws IOException {
        testUserFile = tempFolder.newFile("users.json");
        testGameDataFile = tempFolder.newFile("gamedata.json");
        
        writer = new GameDataWriter(
            testUserFile.getAbsolutePath(),
            testGameDataFile.getAbsolutePath()
        );
    }
    
    @After
    public void tearDown() {
        writer = null;
    }
    
    @Test
    public void testWriteUsersWithEmptyList() {
        List<User> users = new ArrayList<>();
        
        boolean result = writer.writeUsers(users);
        
        assertTrue(result);
    }
    
    @Test
    public void testWriteUsersCreatesFile() {
        List<User> users = new ArrayList<>();
        
        writer.writeUsers(users);
        
        assertTrue(testUserFile.exists());
    }
    
    @Test
    public void testWriteUsersWithNonEmptyList() {
        List<User> users = new ArrayList<>();
        users.add(new User("wrt01", "password", "Write", "Test", "write@test.com"));
        
        boolean result = writer.writeUsers(users);
        
        assertTrue(result);
    }
    
    @Test
    public void testWriteUsersWithMultipleUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("wrt02", "password", "Write", "Test1", "write1@test.com"));
        users.add(new User("wrt03", "password", "Write", "Test2", "write2@test.com"));
        
        boolean result = writer.writeUsers(users);
        
        assertTrue(result);
    }
    
    @Test
    public void testWriteUsersCreatesValidJson() throws IOException {
        List<User> users = new ArrayList<>();
        users.add(new User("usr01", "pass123", "John", "Doe", "john@example.com"));
        
        writer.writeUsers(users);
        String content = new String(Files.readAllBytes(testUserFile.toPath()));
        
        assertTrue(content.contains("usr01"));
    }
    
    @Test
    public void testWriteGameDataWithEmptyData() {
        GameData gameData = new GameData();
        
        boolean result = writer.writeGameData(gameData);
        
        assertTrue(result);
    }
    
    @Test
    public void testWriteGameDataCreatesFile() {
        GameData gameData = new GameData();
        
        writer.writeGameData(gameData);
        
        assertTrue(testGameDataFile.exists());
    }
    
    @Test
    public void testWriteGameDataWithPuzzles() {
        GameData gameData = new GameData();
        Puzzle puzzle = new Puzzle();
        puzzle.setPuzzleId("p1");
        puzzle.setTitle("Test Puzzle");
        puzzle.setDifficulty("EASY");
        puzzle.setPuzzleType("MAZE");
        gameData.getPuzzles().add(puzzle);
        
        boolean result = writer.writeGameData(gameData);
        
        assertTrue(result);
    }
    
    @Test
    public void testWriteGameDataWithHints() {
        GameData gameData = new GameData();
        Hint hint = new Hint("Test hint", "p1", 1);
        gameData.getHints().add(hint);
        
        boolean result = writer.writeGameData(gameData);
        
        assertTrue(result);
    }
    
    @Test
    public void testWriteGameDataCreatesValidJson() throws IOException {
        GameData gameData = new GameData();
        Puzzle puzzle = new Puzzle();
        puzzle.setPuzzleId("p1");
        puzzle.setTitle("Test Puzzle");
        gameData.getPuzzles().add(puzzle);
        
        writer.writeGameData(gameData);
        String content = new String(Files.readAllBytes(testGameDataFile.toPath()));
        
        assertTrue(content.contains("Test Puzzle"));
    }
    
    @Test
    public void testWriteGameDataPreservesPuzzles() throws IOException {
        GameData gameData = new GameData();
        Puzzle puzzle1 = new Puzzle();
        puzzle1.setPuzzleId("p1");
        puzzle1.setTitle("Puzzle 1");
        Puzzle puzzle2 = new Puzzle();
        puzzle2.setPuzzleId("p2");
        puzzle2.setTitle("Puzzle 2");
        gameData.getPuzzles().add(puzzle1);
        gameData.getPuzzles().add(puzzle2);
        
        writer.writeGameData(gameData);
        String content = new String(Files.readAllBytes(testGameDataFile.toPath()));
        
        assertTrue(content.contains("Puzzle 1") && content.contains("Puzzle 2"));
    }
    
    @Test
    public void testWriteGameDataPreservesHints() throws IOException {
        GameData gameData = new GameData();
        Hint hint1 = new Hint("Hint 1", "p1", 1);
        Hint hint2 = new Hint("Hint 2", "p1", 2);
        gameData.getHints().add(hint1);
        gameData.getHints().add(hint2);
        
        writer.writeGameData(gameData);
        String content = new String(Files.readAllBytes(testGameDataFile.toPath()));
        
        assertTrue(content.contains("Hint 1") && content.contains("Hint 2"));
    }
}

