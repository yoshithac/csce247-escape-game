package com.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Singleton Facade for all game data operations
 * Delegates to GameDataLoader and GameDataWriter
 * Provides unified access to all game data
 */
public class GameDataFacade {
    private static GameDataFacade instance;
    
    private final GameDataLoader loader;
    private final GameDataWriter writer;
    
    // In-memory data stores
    private List<User> users;
    private GameData gameData;
    
    // Private constructor for singleton
    private GameDataFacade() {
        this.loader = new GameDataLoader();
        this.writer = new GameDataWriter();
        loadAllData();
    }

    // Test constructor - allows dependency injection
    protected GameDataFacade(GameDataLoader loader, GameDataWriter writer) {
        this.loader = loader;
        this.writer = writer;
        loadAllData();
    }

    // Test method to inject custom instance
    protected static void setTestInstance(GameDataFacade testInstance) {
        instance = testInstance;
    }

    /**
    * Reset singleton instance - USE ONLY FOR TESTING
    * This allows tests to start with a fresh instance
    */
    protected static void resetInstance() {
        instance = null;
    }

    /**
     * Get singleton instance
     */
    public static GameDataFacade getInstance() {
        if (instance == null) {
            instance = new GameDataFacade();
        }
        return instance;
    }
    
    /**
     * Load all data from JSON files
     */
    private void loadAllData() {
        this.users = loader.readUsers();
        this.gameData = loader.readGameData();
    }
    
    /**
     * Save all data to JSON files
     */
    private void saveAllData() {
        writer.writeUsers(users);
        writer.writeGameData(gameData);
    }
    
    // ===== USER OPERATIONS =====
    
    public Optional<User> getUser(String userId) {
        return users.stream()
            .filter(u -> u.getUserId().equals(userId))
            .findFirst();
    }
    
    /**
     * Add new user if userId and email are unique
     * @param user User to add
     * @return true if added successfully
     */
    public boolean addUser(User user) {
        if (userIdExists(user.getUserId()) || emailExists(user.getEmail())) {
            return false;
        }
        users.add(user);
        
        // Create UserProgress for new user
        UserProgress progress = new UserProgress(user.getUserId());
        gameData.getUserProgress().add(progress);
        
        saveAllData();
        return true;
    }
    
    /**
     * Update existing user details
     * @param user User with updated details
     * @return true if update successful
     */
    public boolean updateUser(User user) {
        Optional<User> existingUser = getUser(user.getUserId());
        if (!existingUser.isPresent()) {
            return false;
        }
        
        users.removeIf(u -> u.getUserId().equals(user.getUserId()));
        users.add(user);
        saveAllData();
        return true;
    }
    
    /**
     * Check if userId exists   
     * @param userId
     * @return true if userId exists
     */
    public boolean userIdExists(String userId) {
        return users.stream().anyMatch(u -> u.getUserId().equals(userId));
    }
    
    /**
     * Check if email exists
     * @param email
     * @return true if email exists
     */
    public boolean emailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equals(email));
    }
    
    // ===== PUZZLE OPERATIONS =====
    
    /**
     * Get all puzzles
     * @return List of all puzzles
     */
    public List<Puzzle> getAllPuzzles() {
        return new ArrayList<>(gameData.getPuzzles());
    }
    
    /**
     * Get puzzle by ID
     * @param puzzleId
     * @return Optional Puzzle
     */
    public Optional<Puzzle> getPuzzle(String puzzleId) {
        return gameData.getPuzzles().stream()
            .filter(p -> p.getPuzzleId().equals(puzzleId))
            .findFirst();
    }
    
    /**
     * Get puzzles by type
     * @param puzzleType
     * @return List of puzzles of given type
     */
    public List<Puzzle> getPuzzlesByType(String puzzleType) {
        return gameData.getPuzzles().stream()
            .filter(p -> p.getPuzzleType().equalsIgnoreCase(puzzleType))
            .collect(Collectors.toList());
    }
    
    /**
     * Get puzzles by type and difficulty
     * @param puzzleType
     * @param difficulty
     * @return List of puzzles matching criteria
     */
    public List<Puzzle> getPuzzlesByDifficulty(String puzzleType, String difficulty) {
        return gameData.getPuzzles().stream()
            .filter(p -> p.getPuzzleType().equalsIgnoreCase(puzzleType))
            .filter(p -> p.getDifficulty().equalsIgnoreCase(difficulty))
            .collect(Collectors.toList());
    }
    
    /**
     * Get available puzzle types
     * @return Set of unique puzzle types
     */
    public Set<String> getAvailablePuzzleTypes() {
        return gameData.getPuzzles().stream()
            .map(Puzzle::getPuzzleType)
            .collect(Collectors.toSet());
    }
    
    // ===== HINT OPERATIONS =====
    
    /**
     * Get hints for a specific puzzle
     * @param puzzleId
     * @return List of hints for the puzzle
     */
    public List<Hint> getHintsForPuzzle(String puzzleId) {
        return gameData.getHints().stream()
            .filter(h -> h.getPuzzleId().equals(puzzleId))
            .sorted()
            .collect(Collectors.toList());
    }
    
    // ===== USER PROGRESS OPERATIONS =====
    
    /**
     * Get user's progress data
     * @param userId User ID
     * @return UserProgress object (never null)
     */
    public UserProgress getUserProgress(String userId) {
        Optional<UserProgress> progress = gameData.getUserProgress().stream()
            .filter(up -> up.getUserId().equals(userId))
            .findFirst();
        
        if (progress.isPresent()) {
            return progress.get();
        } else {
            // Create new progress for user
            UserProgress newProgress = new UserProgress(userId);
            gameData.getUserProgress().add(newProgress);
            saveAllData();
            return newProgress;
        }
    }
    
    /**
     * Save user's progress data
     * @param progress UserProgress to save
     */
    public void saveUserProgress(UserProgress progress) {
        gameData.getUserProgress().removeIf(up -> up.getUserId().equals(progress.getUserId()));
        gameData.getUserProgress().add(progress);
        
        // Update leaderboard
        updateLeaderboard(progress);
        
        saveAllData();
    }
    
    /**
     * Check if user has completed a specific puzzle
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return true if puzzle is completed
     */
    public boolean isPuzzleCompleted(String userId, String puzzleId) {
        UserProgress progress = getUserProgress(userId);
        return progress.isPuzzleCompleted(puzzleId);
    }
    
    /**
     * Mark puzzle as completed for user
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @param score Score achieved
     */
    public void completePuzzle(String userId, String puzzleId, int score) {
        UserProgress progress = getUserProgress(userId);
        progress.addCompletedPuzzle(puzzleId, score);
        progress.clearGameState(); // Clear any saved game
        saveUserProgress(progress);
    }

    // ===== CERTIFICATE OPERATIONS =====
    
    /**
     * Add a new certificate
     * @param certificate Certificate to add
     */
    public void addCertificate(Certificate certificate) {
        gameData.getCertificates().add(certificate);
        saveAllData();
    }
    
    /**
     * Get certificates earned by user
     * @param userId User ID
     * @return List of certificates
     */
    public List<Certificate> getUserCertificates(String userId) {
        return gameData.getCertificates().stream()
            .filter(c -> c.getUserId().equals(userId))
            .sorted((c1, c2) -> c2.getEarnedAt().compareTo(c1.getEarnedAt()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get certificate statistics for user
     * @param userId User ID
     * @return Map of difficulty to count
     */
    public Map<String, Integer> getCertificateStats(String userId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("EASY", 0);
        stats.put("MEDIUM", 0);
        stats.put("HARD", 0);
        
        gameData.getCertificates().stream()
            .filter(c -> c.getUserId().equals(userId))
            .forEach(c -> {
                String difficulty = c.getDifficulty().toUpperCase();
                stats.put(difficulty, stats.get(difficulty) + 1);
            });
        
        return stats;
    }
    
    /**
     * Check if user has a certificate for a specific puzzle
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return true if certificate exists
     */
    public boolean hasCertificate(String userId, String puzzleId) {
        return gameData.getCertificates().stream()
            .anyMatch(c -> c.getUserId().equals(userId) && c.getPuzzleId().equals(puzzleId));
    }
    
    // ===== LEADERBOARD OPERATIONS =====
    
    /**
     * Update leaderboard entry for user
     * @param progress UserProgress
     */
    private void updateLeaderboard(UserProgress progress) {
        String userId = progress.getUserId();
        Optional<User> userOpt = getUser(userId);
        
        if (!userOpt.isPresent()) {
            return;
        }
        
        User user = userOpt.get();
        
        // Remove existing entry
        gameData.getLeaderboard().removeIf(e -> e.getUserId().equals(userId));
        
        // Add new entry
        LeaderboardEntry entry = new LeaderboardEntry(
            userId,
            user.getFullName(),
            progress.getTotalScore(),
            progress.getCompletedCount()
        );
        gameData.getLeaderboard().add(entry);
        
        // Sort leaderboard by score (descending)
        gameData.getLeaderboard().sort((e1, e2) -> 
            Integer.compare(e2.getTotalScore(), e1.getTotalScore()));
    }
    
    /**
     * Get top N leaderboard entries
     * @param limit Number of entries to return
     * @return List of leaderboard entries
     */
    public List<LeaderboardEntry> getLeaderboard(int limit) {
        return gameData.getLeaderboard().stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Get user's rank on leaderboard
     * @param userId User ID
     * @return Rank (1-based), or -1 if not found
     */
    public int getUserRank(String userId) {
        List<LeaderboardEntry> leaderboard = gameData.getLeaderboard();
        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return -1;
    }

    public List<User> getUsers() {
        return users;
    }
}
