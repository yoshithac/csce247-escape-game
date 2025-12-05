package com.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GameServiceManager - Unified Singleton Service Manager
 * 
 * Combines ALL services into one centralized manager:
 * - Authentication (login/logout/registration)
 * - Game Progress (puzzles, scoring)
 * - User Management
 * - Leaderboard
 * - Certificates
 * - Puzzle Management
 * - Hints
 * - Game Session Management
 * 
 * NOTE: Timer functionality moved to SessionTimer utility class.
 * Controllers use SessionTimer directly and bridge elapsed time to/from this class.
 * 
 * SINGLE POINT OF ACCESS for entire application
 * 
 * @author Escape Game Team
 * @version 2.3
 */
public class GameServiceManager {
    
    private static GameServiceManager instance = null;
    private final GameDataFacade dataFacade;
    private final AuthenticationService authService;       // Delegated authentication
    private final GameProgressService progressService;     // Delegated progress management
    private final LeaderboardService leaderboardService;   // Delegated leaderboard
    private final CertificateService certificateService;
    
    // Session time limits per difficulty (in seconds)
    public static final int EASY_TIME_LIMIT = 600;    // 10 minutes
    public static final int MEDIUM_TIME_LIMIT = 480;  // 8 minutes
    public static final int HARD_TIME_LIMIT = 360;    // 6 minutes
    public static final String[] PUZZLE_TYPES = {"MAZE", "MATCHING", "CIPHER", "ANAGRAM", "RIDDLE"};
    
    // Current session state (in-memory cache, persisted via UserProgress.sessionState)
    private Map<Integer, String> sessionDoorToPuzzleMap;  // Door number -> Puzzle ID
    private Set<Integer> sessionCompletedDoors;           // Doors completed DURING this session
    private String sessionDifficulty;
    private int sessionElapsedSeconds;
    private int sessionTimeLimit;  // Time limit for current session based on difficulty
    
    /**
     * Private constructor - prevents external instantiation
     */
    private GameServiceManager() {
        this.dataFacade = GameDataFacade.getInstance();
        this.authService = new AuthenticationService();
        this.progressService = new GameProgressService();
        this.leaderboardService = new LeaderboardService();
        this.certificateService = new CertificateService();
        this.sessionDoorToPuzzleMap = new HashMap<>();
        this.sessionCompletedDoors = new HashSet<>();
    }
    
    /**
     * Get singleton instance
     * Thread-safe singleton pattern
     */
    public static synchronized GameServiceManager getInstance() {
        if (instance == null) {
            instance = new GameServiceManager();
        }
        return instance;
    }
    
    // ================================================================
    // AUTHENTICATION METHODS (Delegated to AuthenticationService)
    // ================================================================
    
    /**
     * Authenticate user with userId and password
     * @param userId User's ID
     * @param password User's password
     * @return true if login successful
     */
    public boolean login(String userId, String password) {
        return authService.login(userId, password);
    }
    
    /**
     * Register a new user
     * @param userId Unique user ID (5 characters)
     * @param password Password (min 4 characters)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @return true if registration successful
     */
    public boolean register(String userId, String password, String firstName,
                          String lastName, String email) {
        return authService.register(userId, password, firstName, lastName, email);
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        authService.logout();
        clearSessionCache();
    }
    
    /**
     * Check if user is logged in
     * @return true if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }
    
    /**
     * Get current logged-in user
     * @return Current User or null if not logged in
     */
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }
    
    /**
     * Attempt login with validation
     * @param userId User ID
     * @param password Password
     * @return LoginResult with success status and message
     */
    public AuthenticationService.LoginResult attemptLogin(String userId, String password) {
        return authService.attemptLogin(userId, password);
    }
    
    /**
     * Attempt registration with validation
     * @param userId User ID
     * @param password Password
     * @param firstName First name
     * @param lastName Last name
     * @param email Email
     * @return RegistrationResult with success status and message
     */
    public AuthenticationService.RegistrationResult attemptRegistration(String userId, String password,
            String firstName, String lastName, String email) {
        return authService.attemptRegistration(userId, password, firstName, lastName, email);
    }
    
    // ================================================================
    // USER MANAGEMENT METHODS
    // ================================================================
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return Optional containing User if found
     */
    public Optional<User> getUser(String userId) {
        return dataFacade.getUser(userId);
    }
    
    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return dataFacade.getUsers();
    }
    
    /**
     * Update user information
     * @param user User to update
     * @return true if update successful
     */
    public boolean updateUser(User user) {
        return dataFacade.updateUser(user);
    }
    
    /**
     * Check if userId exists
     * @param userId User ID to check
     * @return true if userId is already taken
     */
    public boolean userIdExists(String userId) {
        return dataFacade.userIdExists(userId);
    }
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if email is already registered
     */
    public boolean emailExists(String email) {
        return dataFacade.emailExists(email);
    }
    
    // ================================================================
    // GAME PROGRESS METHODS (Delegated to GameProgressService)
    // ================================================================
    
    /**
     * Get user's progress data
     * @param userId User ID
     * @return UserProgress object or null if not found
     */
    public UserProgress getUserProgress(String userId) {
        return progressService.getUserProgress(userId);
    }
    
    /**
     * Save user progress
     * @param progress UserProgress to save
     */
    public void saveUserProgress(UserProgress progress) {
        progressService.saveUserProgress(progress);
    }
    
    /**
     * Mark puzzle as completed
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @param score Score achieved
     */
    public void completePuzzle(String userId, String puzzleId, int score) {
        progressService.completePuzzle(userId, puzzleId, score);
    }
    
    /**
     * Check if puzzle is completed
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return true if user has completed this puzzle
     */
    public boolean isPuzzleCompleted(String userId, String puzzleId) {
        return progressService.isPuzzleCompleted(userId, puzzleId);
    }
    
    /**
     * Get user's total score
     * @param userId User ID
     * @return Total score across all puzzles
     */
    public int getUserTotalScore(String userId) {
        return progressService.getUserTotalScore(userId);
    }
    
    /**
     * Get count of completed puzzles
     * @param userId User ID
     * @return Number of puzzles completed
     */
    public int getCompletedPuzzleCount(String userId) {
        return progressService.getCompletedPuzzleCount(userId);
    }
    
    /**
     * Save game state (for resuming)
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @param state Game state as Map
     */
    public void saveGameState(String userId, String puzzleId, Map<String, Object> state) {
        progressService.saveGameState(userId, puzzleId, state);
    }
    
    /**
     * Get saved game state
     * @param userId User ID
     * @return Game state Map or null
     */
    public Map<String, Object> getGameState(String userId) {
        return progressService.getGameState(userId);
    }
    
    /**
     * Check if user has a game in progress
     * @param userId User ID
     * @return true if there's a saved game
     */
    public boolean hasGameInProgress(String userId) {
        return progressService.hasGameInProgress(userId);
    }
    
    /**
     * Get current puzzle ID being played
     * @param userId User ID
     * @return Puzzle ID or null
     */
    public String getCurrentPuzzleId(String userId) {
        return progressService.getCurrentPuzzleId(userId);
    }
    
    /**
     * Clear game state (after completing)
     * @param userId User ID
     */
    public void clearGameState(String userId) {
        progressService.clearGameState(userId);
    }
    
    // ================================================================
    // PAUSED PUZZLE METHODS (Delegated to GameProgressService)
    // ================================================================
    
    /**
     * Save paused puzzle state (for resuming individual puzzles)
     * @param puzzleId Puzzle ID
     * @param state Game state to save
     */
    public void savePausedPuzzle(String puzzleId, Map<String, Object> state) {
        User user = getCurrentUser();
        if (user != null) {
            progressService.savePausedPuzzle(user.getUserId(), puzzleId, state);
        }
    }
    
    /**
     * Get paused puzzle state
     * @return Paused puzzle state or null
     */
    public Map<String, Object> getPausedPuzzleState() {
        User user = getCurrentUser();
        return user != null ? progressService.getPausedPuzzleState(user.getUserId()) : null;
    }
    
    /**
     * Clear paused puzzle state (after completing puzzle)
     */
    public void clearPausedPuzzle() {
        User user = getCurrentUser();
        if (user != null) {
            progressService.clearPausedPuzzle(user.getUserId());
        }
    }
    
    /**
     * Check if user has a paused puzzle
     * @return true if there's a paused puzzle
     */
    public boolean hasPausedPuzzle() {
        User user = getCurrentUser();
        return user != null && progressService.hasPausedPuzzle(user.getUserId());
    }
    
    /**
     * Get current paused puzzle ID
     * @return Puzzle ID or null
     */
    public String getPausedPuzzleId() {
        User user = getCurrentUser();
        return user != null ? progressService.getPausedPuzzleId(user.getUserId()) : null;
    }
    
    /**
     * Check if user has a saved session
     * @return true if there's a saved session
     */
    public boolean hasSavedSession() {
        User user = getCurrentUser();
        return user != null && progressService.hasSavedSession(user.getUserId());
    }
    
    /**
     * Get saved session state
     * @return Session state map or null
     */
    public Map<String, Object> getSavedSessionState() {
        User user = getCurrentUser();
        return user != null ? progressService.getSavedSessionState(user.getUserId()) : null;
    }
    
    // ================================================================
    // PUZZLE METHODS
    // ================================================================
    
    /**
     * Get all puzzles
     * @return List of all puzzles
     */
    public List<Puzzle> getAllPuzzles() {
        return dataFacade.getAllPuzzles();
    }
    
    /**
     * Get specific puzzle by ID
     * @param puzzleId Puzzle ID
     * @return Optional containing Puzzle if found
     */
    public Optional<Puzzle> getPuzzle(String puzzleId) {
        return dataFacade.getPuzzle(puzzleId);
    }
    
    /**
     * Get puzzles by type
     * @param type Puzzle type (MAZE, MATCHING, CIPHER, etc.)
     * @return List of puzzles of that type
     */
    public List<Puzzle> getPuzzlesByType(String type) {
        return dataFacade.getPuzzlesByType(type);
    }
    
    /**
     * Get puzzles by type and difficulty
     * @param puzzleType Puzzle type
     * @param difficulty Difficulty level (EASY, MEDIUM, HARD)
     * @return List of matching puzzles
     */
    public List<Puzzle> getPuzzlesByDifficulty(String puzzleType, String difficulty) {
        return dataFacade.getPuzzlesByDifficulty(puzzleType, difficulty);
    }
    
    /**
     * Get available puzzle types
     * @return Set of puzzle type strings
     */
    public Set<String> getAvailablePuzzleTypes() {
        return dataFacade.getAvailablePuzzleTypes();
    }
    
    // ================================================================
    // HINT METHODS
    // ================================================================
    
    /**
     * Get hints for puzzle
     * @param puzzleId Puzzle ID
     * @return List of hints ordered by priority
     */
    public List<Hint> getHintsForPuzzle(String puzzleId) {
        return dataFacade.getHintsForPuzzle(puzzleId);
    }
    
    // ================================================================
    // CERTIFICATE METHODS
    // ================================================================
    
    /**
     * Get user's certificates
     * @param userId User ID
     * @return List of certificates earned
     */
    public List<Certificate> getUserCertificates(String userId) {
        return dataFacade.getUserCertificates(userId);
    }
    
    /**
     * Award certificate for puzzle completion
     * @param userId User ID
     * @param puzzle Completed puzzle
     * @param score Score achieved
     */
    public void awardCertificate(String userId, Puzzle puzzle, int score) {
        String certId = "CERT_" + userId + "_" + puzzle.getPuzzleId() + "_" + System.currentTimeMillis();
        String description = String.format("Completed '%s' puzzle", puzzle.getTitle());
        
        Certificate cert = new Certificate(
            certId,
            userId,
            puzzle.getPuzzleId(),
            description,
            puzzle.getDifficulty(),
            score
        );
        
        dataFacade.addCertificate(cert);
    }
    
    /**
     * Get certificate statistics for user
     * @param userId User ID
     * @return Map of certificate stats
     */
    public Map<String, Integer> getCertificateStats(String userId) {
        return dataFacade.getCertificateStats(userId);
    }
    
    /**
     * Check if user has certificate for puzzle
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return true if certificate exists
     */
    public boolean hasCertificate(String userId, String puzzleId) {
        return dataFacade.hasCertificate(userId, puzzleId);
    }
    
    /**
     * Award certificate for completing a game session (all 5 puzzles)
     * 
     * @param userId User ID
     * @param difficulty Session difficulty (EASY, MEDIUM, HARD)
     * @param totalScore Combined score from all 5 puzzles
     * @param completionTimeSeconds Time taken to complete session in seconds
     * @return The created Certificate
     */
    public Certificate awardSessionCertificate(String userId, String difficulty, 
                                                int totalScore, int completionTimeSeconds) {
        return certificateService.awardSessionCertificate(userId, difficulty, totalScore, completionTimeSeconds);
    }
    
    /**
     * Get comprehensive certificate statistics
     * @param userId User ID
     * @return Map containing various statistics (counts, scores, times)
     */
    public Map<String, Object> getUserCertificateStats(String userId) {
        return certificateService.getUserCertificateStats(userId);
    }
    
    /**
     * Get best (fastest) completion time for a difficulty
     * @param userId User ID
     * @param difficulty Difficulty level
     * @return Best time in seconds, or -1 if no completions
     */
    public int getBestCompletionTime(String userId, String difficulty) {
        return certificateService.getBestTime(userId, difficulty);
    }
    
    /**
     * Get best score for a difficulty
     * @param userId User ID
     * @param difficulty Difficulty level
     * @return Best score, or 0 if no completions
     */
    public int getBestCertificateScore(String userId, String difficulty) {
        return certificateService.getBestScore(userId, difficulty);
    }
    
    /**
     * Get leaderboard comparison data for certificate display
     * @param userId User ID
     * @return Map containing rank, percentile, and comparison data
     */
    public Map<String, Object> getLeaderboardComparison(String userId) {
        return certificateService.getLeaderboardComparison(userId);
    }
    
    // ================================================================
    // LEADERBOARD METHODS (Delegated to LeaderboardService)
    // ================================================================
    
    /**
     * Get top N players
     * @param limit Maximum number of entries
     * @return List of top leaderboard entries
     */
    public List<LeaderboardEntry> getTopPlayers(int limit) {
        return leaderboardService.getTopPlayers(limit);
    }
    
    /**
     * Get user's rank on leaderboard (competition ranking - ties get same rank)
     * @param userId User ID
     * @return Rank position (1-based) or -1 if not found
     */
    public int getUserRank(String userId) {
        return leaderboardService.getUserRank(userId);
    }
    
    /**
     * Calculate competition rank for a leaderboard entry
     * Players with same score get same rank
     * @param entry LeaderboardEntry to calculate rank for
     * @return Rank (1-based) or -1 if entry is null
     */
    public int calculateRank(LeaderboardEntry entry) {
        return leaderboardService.calculateRank(entry);
    }
    
    /**
     * Get leaderboard entry for specific user
     * @param userId User ID
     * @return LeaderboardEntry or null
     */
    public LeaderboardEntry getUserLeaderboardEntry(String userId) {
        return leaderboardService.getUserLeaderboardEntry(userId);
    }
    
    /**
     * Get full leaderboard (all entries)
     * @return List of all leaderboard entries
     */
    public List<LeaderboardEntry> getFullLeaderboard() {
        return leaderboardService.getFullLeaderboard();
    }
    
    // ================================================================
    // PROGRESS STATISTICS (Delegated to GameProgressService)
    // ================================================================
    
    /**
     * Get completed puzzles for user
     * @param userId User ID
     * @return List of completed puzzle IDs
     */
    public List<String> getCompletedPuzzles(String userId) {
        return progressService.getCompletedPuzzleIds(userId);
    }
    
    /**
     * Get puzzle scores for user
     * @param userId User ID
     * @return Map of puzzleId -> score
     */
    public Map<String, Integer> getPuzzleScores(String userId) {
        return progressService.getPuzzleScores(userId);
    }
    
    /**
     * Get best score for a puzzle
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return Best score or 0 if not completed
     */
    public int getBestScore(String userId, String puzzleId) {
        return progressService.getBestScore(userId, puzzleId);
    }
    
    // ================================================================
    // GAME SESSION METHODS (NEW)
    // ================================================================
    
    /**
     * Get session time limit based on difficulty
     * @param difficulty Session difficulty (EASY, MEDIUM, HARD)
     * @return Time limit in seconds
     */
    public static int getTimeLimitForDifficulty(String difficulty) {
        if (difficulty == null) return EASY_TIME_LIMIT;
        switch (difficulty.toUpperCase()) {
            case "EASY":   return EASY_TIME_LIMIT;    // 10 minutes
            case "MEDIUM": return MEDIUM_TIME_LIMIT;  // 8 minutes
            case "HARD":   return HARD_TIME_LIMIT;    // 6 minutes
            default:       return EASY_TIME_LIMIT;    // fallback
        }
    }
    
    /**
     * Get current session time limit
     * @return Time limit in seconds for current session
     */
    public int getSessionTimeLimit() {
        return sessionTimeLimit > 0 ? sessionTimeLimit : getTimeLimitForDifficulty(sessionDifficulty);
    }
    
    /**
     * Start a new game session
     * Assigns one random puzzle per type to doors 1-5
     * Prefers uncompleted puzzles, but allows replays if all are completed
     * @param difficulty Session difficulty (EASY, MEDIUM, HARD)
     */
    public void startNewSession(String difficulty) {
        if (getCurrentUser() == null) return;
        
        String userId = getCurrentUser().getUserId();
        this.sessionDifficulty = difficulty;
        this.sessionTimeLimit = getTimeLimitForDifficulty(difficulty);
        this.sessionDoorToPuzzleMap.clear();
        this.sessionCompletedDoors.clear();  // Fresh session = no doors completed yet
        this.sessionElapsedSeconds = 0;
        
        // Get user's completed puzzles from UserProgress
        UserProgress progress = dataFacade.getUserProgress(userId);
        List<String> completedPuzzles = progress.getCompletedPuzzles();
        
        System.out.println("=== Starting New Session ===");
        System.out.println("User: " + userId);
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Already completed puzzles: " + completedPuzzles);
        
        // Shuffle puzzle types for random door assignment
        List<String> shuffledTypes = new ArrayList<>(Arrays.asList(PUZZLE_TYPES));
        Collections.shuffle(shuffledTypes);
        
        // Assign one puzzle per door
        for (int doorNum = 1; doorNum <= 5; doorNum++) {
            String puzzleType = shuffledTypes.get(doorNum - 1);
            List<Puzzle> puzzles = dataFacade.getPuzzlesByDifficulty(puzzleType, difficulty);
            
            if (!puzzles.isEmpty()) {
                // First, try to find an UNCOMPLETED puzzle
                List<Puzzle> uncompletedPuzzles = puzzles.stream()
                    .filter(p -> !completedPuzzles.contains(p.getPuzzleId()))
                    .collect(Collectors.toList());
                
                String puzzleId;
                if (!uncompletedPuzzles.isEmpty()) {
                    // Pick random uncompleted puzzle
                    Collections.shuffle(uncompletedPuzzles);
                    puzzleId = uncompletedPuzzles.get(0).getPuzzleId();
                    System.out.println("Door " + doorNum + " -> " + puzzleType + " -> " + puzzleId + " (NEW)");
                } else {
                    // All puzzles completed, pick random for replay
                    Collections.shuffle(puzzles);
                    puzzleId = puzzles.get(0).getPuzzleId();
                    System.out.println("Door " + doorNum + " -> " + puzzleType + " -> " + puzzleId + " (REPLAY - all completed)");
                }
                
                sessionDoorToPuzzleMap.put(doorNum, puzzleId);
            } else {
                System.err.println("Door " + doorNum + " -> " + puzzleType + " -> NO PUZZLES FOUND!");
            }
        }
        
        // Save to database
        saveSessionToDatabase();
        
        System.out.println("=== Session Started ===");
    }
    
    /**
     * Restore existing session from database
     * @return true if session was restored, false if no saved session
     */
    @SuppressWarnings("unchecked")
    public boolean restoreSession() {
        if (getCurrentUser() == null) return false;
        
        String userId = getCurrentUser().getUserId();
        UserProgress progress = dataFacade.getUserProgress(userId);
        Map<String, Object> savedSession = progress.getSessionState();
        
        System.out.println("=== restoreSession() ===");
        System.out.println("savedSession: " + savedSession);
        
        if (savedSession == null || !savedSession.containsKey("sessionDifficulty")) {
            System.out.println("No saved session found");
            return false;
        }
        
        // Restore session data
        sessionDifficulty = (String) savedSession.get("sessionDifficulty");
        sessionTimeLimit = getTimeLimitForDifficulty(sessionDifficulty);
        sessionElapsedSeconds = ((Number) savedSession.getOrDefault("elapsedSeconds", 0)).intValue();
        
        System.out.println("Restored elapsedSeconds: " + sessionElapsedSeconds);
        System.out.println("Remaining time: " + getSessionRemainingSeconds() + "s");
        
        // Restore door to puzzle mapping
        sessionDoorToPuzzleMap.clear();
        sessionCompletedDoors.clear();
        
        Map<String, String> savedDoorPuzzles = (Map<String, String>) savedSession.get("doorPuzzleMap");
        if (savedDoorPuzzles != null) {
            for (Map.Entry<String, String> entry : savedDoorPuzzles.entrySet()) {
                int doorNum = Integer.parseInt(entry.getKey());
                String puzzleId = entry.getValue();
                sessionDoorToPuzzleMap.put(doorNum, puzzleId);
            }
        }
        
        // Restore completed doors for this session
        List<Number> savedCompletedDoors = (List<Number>) savedSession.get("completedDoors");
        if (savedCompletedDoors != null) {
            for (Number doorNum : savedCompletedDoors) {
                sessionCompletedDoors.add(doorNum.intValue());
            }
        }
        
        System.out.println("Restored session - completed doors: " + sessionCompletedDoors);
        return true;
    }
    
    /**
     * Save current session state to database
     */
    public void saveSessionToDatabase() {
        if (getCurrentUser() == null) {
            System.out.println("saveSessionToDatabase: No current user, skipping");
            return;
        }
        
        if (sessionDifficulty == null) {
            System.out.println("saveSessionToDatabase: No session difficulty set, skipping");
            return;
        }
        
        System.out.println("=== saveSessionToDatabase() ===");
        System.out.println("Saving elapsedSeconds: " + sessionElapsedSeconds);
        System.out.println("Session difficulty: " + sessionDifficulty);
        
        UserProgress progress = dataFacade.getUserProgress(getCurrentUser().getUserId());
        
        Map<String, Object> sessionState = new HashMap<>();
        sessionState.put("sessionDifficulty", sessionDifficulty);
        sessionState.put("elapsedSeconds", sessionElapsedSeconds);
        
        // Convert door map to string keys for JSON
        Map<String, String> doorPuzzleMap = new HashMap<>();
        for (Map.Entry<Integer, String> entry : sessionDoorToPuzzleMap.entrySet()) {
            doorPuzzleMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        sessionState.put("doorPuzzleMap", doorPuzzleMap);
        
        // Save completed doors for this session
        sessionState.put("completedDoors", new ArrayList<>(sessionCompletedDoors));
        
        progress.saveSessionState(sessionState);
        dataFacade.saveUserProgress(progress);
        
        System.out.println("Session saved to database");
    }
    
    /**
     * Clear session from database and cache
     */
    public void clearSession() {
        if (getCurrentUser() != null) {
            UserProgress progress = dataFacade.getUserProgress(getCurrentUser().getUserId());
            progress.clearSessionState();
            dataFacade.saveUserProgress(progress);
        }
        clearSessionCache();
    }
    
    /**
     * End session due to timeout - clears all session state
     * Called when session timer reaches 0 during puzzle play
     * Note: Controllers should call SessionTimer.stop() before calling this
     */
    public void endSessionOnTimeout() {
        System.out.println("=== Session ended due to timeout ===");
        
        // Note: Timer is stopped by Controller using SessionTimer utility
        
        // Clear paused puzzle state
        clearPausedPuzzle();
        
        // Clear session from database and memory
        clearSession();
        
        System.out.println("Session cleared - user must start new game");
    }
    
    /**
     * Clear in-memory session cache
     */
    private void clearSessionCache() {
        sessionDoorToPuzzleMap.clear();
        sessionCompletedDoors.clear();
        sessionDifficulty = null;
        sessionElapsedSeconds = 0;
        sessionTimeLimit = 0;
    }
    
    /**
     * Get puzzle for a specific door
     * @param doorNum Door number (1-5)
     * @return Optional containing Puzzle if found
     */
    public Optional<Puzzle> getSessionPuzzleForDoor(int doorNum) {
        String puzzleId = sessionDoorToPuzzleMap.get(doorNum);
        if (puzzleId == null) return Optional.empty();
        return dataFacade.getPuzzle(puzzleId);
    }
    
    /**
     * Check if door's puzzle is completed
     * @param doorNum Door number (1-5)
     * @return true if completed
     */
    public boolean isSessionDoorCompleted(int doorNum) {
        return sessionCompletedDoors.contains(doorNum);
    }
    
    /**
     * Mark a door as completed in the current session
     * Call this when a puzzle is completed
     * @param doorNum Door number (1-5)
     */
    public void markSessionDoorCompleted(int doorNum) {
        if (doorNum >= 1 && doorNum <= 5) {
            sessionCompletedDoors.add(doorNum);
            System.out.println("✓ Door " + doorNum + " marked as completed in session");
            System.out.println("Total completed doors: " + sessionCompletedDoors.size());
        }
    }
    
    /**
     * Refresh session completion status
     * Note: sessionCompletedDoors is the source of truth for THIS session
     * This method is called when returning from puzzle - the puzzle controller
     * already called markSessionDoorCompleted() so we just log the status
     */
    public void refreshSessionCompletionStatus() {
        if (getCurrentUser() == null) return;
        
        System.out.println("=== Session Completion Status ===");
        System.out.println("Door to puzzle map: " + sessionDoorToPuzzleMap);
        System.out.println("Completed doors this session: " + sessionCompletedDoors);
        System.out.println("Total completed: " + sessionCompletedDoors.size() + "/5");
    }
    
    /**
     * Get count of completed doors in session
     * @return Number of completed doors (0-5)
     */
    public int getSessionCompletedCount() {
        return sessionCompletedDoors.size();
    }
    
    /**
     * Get all completed door numbers
     * @return Unmodifiable set of completed door numbers
     */
    public Set<Integer> getSessionCompletedDoors() {
        return Collections.unmodifiableSet(sessionCompletedDoors);
    }
    
    /**
     * Check if all 5 puzzles are completed (victory)
     * @return true if session is complete
     */
    public boolean isSessionComplete() {
        return sessionCompletedDoors.size() >= 5;
    }
    
    /**
     * Check if session time is up
     * @return true if elapsed time >= time limit
     */
    public boolean isSessionTimeUp() {
        return sessionElapsedSeconds >= getSessionTimeLimit();
    }
    
    /**
     * Get remaining session time in seconds
     * @return Remaining seconds
     */
    public int getSessionRemainingSeconds() {
        return Math.max(0, getSessionTimeLimit() - sessionElapsedSeconds);
    }
    
    /**
     * Get elapsed session time
     * @return Elapsed seconds
     */
    public int getSessionElapsedSeconds() {
        return sessionElapsedSeconds;
    }
    
    /**
     * Set elapsed session time
     * @param seconds Elapsed seconds
     */
    public void setSessionElapsedSeconds(int seconds) {
        this.sessionElapsedSeconds = seconds;
    }
    
    /**
     * Increment session time by one second
     */
    public void incrementSessionTime() {
        this.sessionElapsedSeconds++;
    }
    
    /**
     * Get session difficulty
     * @return Difficulty string
     */
    public String getSessionDifficulty() {
        return sessionDifficulty;
    }
    
    /**
     * Check if there is an active session
     * @return true if session has puzzles assigned
     */
    public boolean hasActiveSession() {
        return !sessionDoorToPuzzleMap.isEmpty();
    }
    
    // ================================================================
    // SCORE CALCULATION
    // ================================================================
    
    /**
     * Calculate score based on game result
     * Centralized scoring logic
     * @param result Game result map containing "time" and "moves"
     * @return Calculated score
     */
    public static int calculateScore(Map<String, Object> result) {
        long timeMs = (long) result.getOrDefault("time", 0L);
        int moves = (int) result.getOrDefault("moves", 0);
        
        int baseScore = 100;
        int timeBonus = Math.max(0, 100 - (int)(timeMs / 1000));
        int moveBonus = Math.max(0, 50 - moves);
        
        return baseScore + timeBonus + moveBonus;
    }
    
    // ================================================================
    // VIEW MAPPING
    // ================================================================
    
    /**
     * Get FXML view name for puzzle type
     * @param puzzleType Puzzle type string
     * @return View name (e.g., "MazeView", "MatchingView")
     */
    public static String getViewNameForPuzzleType(String puzzleType) {
        if (puzzleType == null) return "WordPuzzleView";
        
        switch (puzzleType.toUpperCase()) {
            case "MAZE":
                return "MazeView";
            case "MATCHING":
                return "MatchingView";
            case "CIPHER":
            case "ANAGRAM":
            case "RIDDLE":
                return "WordPuzzleView";
            default:
                return "WordPuzzleView";
        }
    }
    
    // ================================================================
    // SESSION TIME METHODS (Plain data - NO JavaFX)
    // Timer functionality moved to com.escapegame.util.SessionTimer
    // Controllers use SessionTimer directly and bridge elapsed time here
    // ================================================================
    
    /**
     * Get formatted time string (MM:SS)
     * @return Formatted time string
     */
    public String getFormattedRemainingTime() {
        int remaining = getSessionRemainingSeconds();
        int minutes = remaining / 60;
        int seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}