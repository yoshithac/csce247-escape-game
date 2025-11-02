package com.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test cases for AuthenticationService using TemporaryFolder
 * Each test runs in complete isolation with temporary files
 */
public class AuthenticationServiceTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private AuthenticationService authService;
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
        authService = new AuthenticationService();
    }
    
    @After
    public void tearDown() {
        if (authService.isLoggedIn()) {
            authService.logout();
        }
        GameDataFacade.resetInstance();
        facade = null;
        authService = null;
    }
    
    private void createTestData() throws IOException {
        String testUsers = "[]";
        Files.write(testUserFile.toPath(), testUsers.getBytes());
        
        String testGameData = "{" +
            "\"puzzles\":[]," +
            "\"hints\":[]," +
            "\"userProgress\":[]," +
            "\"certificates\":[]," +
            "\"leaderboard\":[]" +
            "}";
        Files.write(testGameDataFile.toPath(), testGameData.getBytes());
    }
    
    // ===== REGISTRATION TESTS =====
    
    @Test
    public void testRegisterWithValidDataReturnsTrue() {
        boolean result = authService.register("usr01", "pass123", "John", "Doe", "john@example.com");
        
        assertTrue(result);
    }
    
    @Test
    public void testRegisterCreatesUser() {
        authService.register("usr02", "pass123", "Jane", "Doe", "jane@example.com");
        
        assertTrue(facade.userIdExists("usr02"));
    }
    
    @Test
    public void testRegisterWithNullUserIdReturnsFalse() {
        boolean result = authService.register(null, "pass123", "Test", "User", "test@example.com");
        
        assertFalse(result);
    }
    
    @Test
    public void testRegisterWithEmptyUserIdReturnsFalse() {
        boolean result = authService.register("", "pass123", "Test", "User", "test@example.com");
        
        assertFalse(result);
    }
    
    @Test
    public void testRegisterWithShortUserIdReturnsFalse() {
        boolean result = authService.register("usr", "pass123", "Test", "User", "test@example.com");
        
        assertFalse(result);
    }
    
    @Test
    public void testRegisterWithLongUserIdReturnsFalse() {
        boolean result = authService.register("user01", "pass123", "Test", "User", "test@example.com");
        
        assertFalse(result);
    }
    
    @Test
    public void testRegisterWithShortPasswordReturnsFalse() {
        boolean result = authService.register("usr03", "abc", "Test", "User", "test@example.com");
        
        assertFalse(result);
    }
    
    @Test
    public void testRegisterWithNullPasswordReturnsFalse() {
        boolean result = authService.register("usr04", null, "Test", "User", "test@example.com");
        
        assertFalse(result);
    }
    
    @Test
    public void testRegisterWithDuplicateUserIdReturnsFalse() {
        authService.register("usr05", "pass123", "First", "User", "first@example.com");
        
        boolean result = authService.register("usr05", "pass456", "Second", "User", "second@example.com");
        
        assertFalse(result);
    }
    
    // ===== LOGIN TESTS =====
    
    @Test
    public void testLoginWithValidCredentialsReturnsTrue() {
        authService.register("usr06", "pass123", "John", "Doe", "john6@example.com");
        
        boolean result = authService.login("usr06", "pass123");
        
        assertTrue(result);
    }
    
    @Test
    public void testLoginSetsLoggedInStatus() {
        authService.register("usr07", "pass123", "Jane", "Doe", "jane7@example.com");
        
        authService.login("usr07", "pass123");
        
        assertTrue(authService.isLoggedIn());
    }
    
    @Test
    public void testLoginSetsCurrentUser() {
        authService.register("usr08", "pass123", "Test", "User", "test8@example.com");
        authService.login("usr08", "pass123");
        
        assertNotNull(authService.getCurrentUser());
    }
    
    @Test
    public void testLoginWithWrongPasswordReturnsFalse() {
        authService.register("usr09", "pass123", "Test", "User", "test9@example.com");
        
        boolean result = authService.login("usr09", "wrongpass");
        
        assertFalse(result);
    }
    
    @Test
    public void testLoginWithNonexistentUserReturnsFalse() {
        boolean result = authService.login("usr99", "pass123");
        
        assertFalse(result);
    }
    
    @Test
    public void testLoginDoesNotSetCurrentUserOnFailure() {
        authService.register("usr10", "pass123", "Test", "User", "test10@example.com");
        authService.login("usr10", "wrongpass");
        
        assertNull(authService.getCurrentUser());
    }
    
    @Test
    public void testLoginReturnsCorrectUser() {
        authService.register("usr11", "pass123", "John", "Doe", "john11@example.com");
        authService.login("usr11", "pass123");
        
        assertEquals("usr11", authService.getCurrentUser().getUserId());
    }
    
    // ===== LOGOUT TESTS =====
    
    @Test
    public void testLogoutClearsCurrentUser() {
        authService.register("usr12", "pass123", "Test", "User", "test12@example.com");
        authService.login("usr12", "pass123");
        
        authService.logout();
        
        assertNull(authService.getCurrentUser());
    }
    
    @Test
    public void testLogoutSetsLoggedOutStatus() {
        authService.register("usr13", "pass123", "Test", "User", "test13@example.com");
        authService.login("usr13", "pass123");
        
        authService.logout();
        
        assertFalse(authService.isLoggedIn());
    }
    
    @Test
    public void testLogoutWithoutLoginDoesNotFail() {
        authService.logout();
        
        assertFalse(authService.isLoggedIn());
    }
    
    // ===== SESSION MANAGEMENT TESTS =====
    
    @Test
    public void testIsLoggedInReturnsFalseInitially() {
        boolean loggedIn = authService.isLoggedIn();
        
        assertFalse(loggedIn);
    }
    
    @Test
    public void testGetCurrentUserReturnsNullInitially() {
        User user = authService.getCurrentUser();
        
        assertNull(user);
    }
    
    @Test
    public void testMultipleLoginsUpdateCurrentUser() {
        authService.register("usr14", "pass123", "User", "One", "user14@example.com");
        authService.register("usr15", "pass123", "User", "Two", "user15@example.com");
        
        authService.login("usr14", "pass123");
        authService.logout();
        authService.login("usr15", "pass123");
        
        assertEquals("usr15", authService.getCurrentUser().getUserId());
    }
}
