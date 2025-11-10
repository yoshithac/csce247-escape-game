package com.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test cases for GameDataLoader using TemporaryFolder
 * Each test runs in complete isolation with temporary files
 */
public class GameDataLoaderTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private GameDataLoader loader;
    private File testUserFile;
    private File testGameDataFile;
    
    @Before
    public void setUp() throws IOException {
        // Create temporary test files
        testUserFile = tempFolder.newFile("users.json");
        testGameDataFile = tempFolder.newFile("gamedata.json");
        
        // Initialize loader with test file paths
        loader = new GameDataLoader(
            testUserFile.getAbsolutePath(),
            testGameDataFile.getAbsolutePath()
        );
    }
    
    @After
    public void tearDown() {
        // TemporaryFolder automatically cleans up files
        loader = null;
    }
    
    @Test
    public void testReadUsersReturnsNonNull() throws IOException {
        // Create empty users array
        Files.write(testUserFile.toPath(), "[]".getBytes());
        
        List<User> users = loader.readUsers();
        
        assertNotNull(users);
    }
    
    @Test
    public void testReadUsersReturnsListType() throws IOException {
        Files.write(testUserFile.toPath(), "[]".getBytes());
        
        List<User> users = loader.readUsers();
        
        assertTrue(users instanceof List);
    }
    
    @Test
    public void testReadUsersReturnsEmptyListWhenFileEmpty() throws IOException {
        Files.write(testUserFile.toPath(), "[]".getBytes());
        
        List<User> users = loader.readUsers();
        
        assertTrue(users.isEmpty());
    }
    
    @Test
    public void testReadUsersReturnsSingleUser() throws IOException {
        String userJson = "[{" +
            "\"userId\":\"usr01\"," +
            "\"password\":\"pass123\"," +
            "\"firstName\":\"John\"," +
            "\"lastName\":\"Doe\"," +
            "\"email\":\"john@example.com\"" +
            "}]";
        Files.write(testUserFile.toPath(), userJson.getBytes());
        
        List<User> users = loader.readUsers();
        
        assertEquals(1, users.size());
    }
    
    @Test
    public void testReadUsersReturnsMultipleUsers() throws IOException {
        String userJson = "[" +
            "{\"userId\":\"usr01\",\"password\":\"pass1\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\"}," +
            "{\"userId\":\"usr02\",\"password\":\"pass2\",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane@example.com\"}" +
            "]";
        Files.write(testUserFile.toPath(), userJson.getBytes());
        
        List<User> users = loader.readUsers();
        
        assertEquals(2, users.size());
    }
    
    @Test
    public void testReadUsersHandlesMissingFile() {
        // Delete the file to simulate missing file
        testUserFile.delete();
        
        List<User> users = loader.readUsers();
        
        assertNotNull(users);
    }
    
    @Test
    public void testReadUsersReturnsEmptyListOnMissingFile() {
        testUserFile.delete();
        
        List<User> users = loader.readUsers();
        
        assertTrue(users.isEmpty());
    }
    
    @Test
    public void testReadGameDataReturnsNonNull() throws IOException {
        String gameDataJson = "{\"puzzles\":[],\"hints\":[],\"userProgress\":[],\"certificates\":[],\"leaderboard\":[]}";
        Files.write(testGameDataFile.toPath(), gameDataJson.getBytes());
        
        GameData gameData = loader.readGameData();
        
        assertNotNull(gameData);
    }
    
    @Test
    public void testReadGameDataReturnsGameDataType() throws IOException {
        String gameDataJson = "{\"puzzles\":[],\"hints\":[],\"userProgress\":[],\"certificates\":[],\"leaderboard\":[]}";
        Files.write(testGameDataFile.toPath(), gameDataJson.getBytes());
        
        GameData gameData = loader.readGameData();
        
        assertTrue(gameData instanceof GameData);
    }
    
    @Test
    public void testReadGameDataInitializesPuzzles() throws IOException {
        String gameDataJson = "{\"puzzles\":[],\"hints\":[],\"userProgress\":[],\"certificates\":[],\"leaderboard\":[]}";
        Files.write(testGameDataFile.toPath(), gameDataJson.getBytes());
        
        GameData gameData = loader.readGameData();
        
        assertNotNull(gameData.getPuzzles());
    }
    
    @Test
    public void testReadGameDataInitializesHints() throws IOException {
        String gameDataJson = "{\"puzzles\":[],\"hints\":[],\"userProgress\":[],\"certificates\":[],\"leaderboard\":[]}";
        Files.write(testGameDataFile.toPath(), gameDataJson.getBytes());
        
        GameData gameData = loader.readGameData();
        
        assertNotNull(gameData.getHints());
    }
    
    @Test
    public void testReadGameDataLoadsPuzzles() throws IOException {
        String gameDataJson = "{" +
            "\"puzzles\":[{\"puzzleId\":\"p1\",\"title\":\"Test Puzzle\",\"difficulty\":\"EASY\",\"puzzleType\":\"MAZE\"}]," +
            "\"hints\":[]," +
            "\"userProgress\":[]," +
            "\"certificates\":[]," +
            "\"leaderboard\":[]" +
            "}";
        Files.write(testGameDataFile.toPath(), gameDataJson.getBytes());
        
        GameData gameData = loader.readGameData();
        
        assertEquals(1, gameData.getPuzzles().size());
    }
    
    @Test
    public void testReadGameDataLoadsHints() throws IOException {
        String gameDataJson = "{" +
            "\"puzzles\":[]," +
            "\"hints\":[{\"hintId\":\"h1\",\"puzzleId\":\"p1\",\"hintText\":\"Test hint\",\"level\":1}]," +
            "\"userProgress\":[]," +
            "\"certificates\":[]," +
            "\"leaderboard\":[]" +
            "}";
        Files.write(testGameDataFile.toPath(), gameDataJson.getBytes());
        
        GameData gameData = loader.readGameData();
        
        assertEquals(1, gameData.getHints().size());
    }
    
    @Test
    public void testReadGameDataHandlesMissingFile() {
        testGameDataFile.delete();
        
        GameData gameData = loader.readGameData();
        
        assertNotNull(gameData);
    }
    
    @Test
    public void testReadGameDataCreatesEmptyDataOnMissingFile() {
        testGameDataFile.delete();
        
        GameData gameData = loader.readGameData();
        
        assertNotNull(gameData.getPuzzles());
    }
}
