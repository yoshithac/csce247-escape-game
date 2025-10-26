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
    
    public boolean userIdExists(String userId) {
        return users.stream().anyMatch(u -> u.getUserId().equals(userId));
    }
    
    public boolean emailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equals(email));
    }
    
    // ===== PUZZLE OPERATIONS =====
    
    public List<Puzzle> getAllPuzzles() {
        return new ArrayList<>(gameData.getPuzzles());
    }
    
    public Optional<Puzzle> getPuzzle(String puzzleId) {
        return gameData.getPuzzles().stream()
            .filter(p -> p.getPuzzleId().equals(puzzleId))
            .findFirst();
    }
    
    public List<Puzzle> getPuzzlesByType(String puzzleType) {
        return gameData.getPuzzles().stream()
            .filter(p -> p.getPuzzleType().equalsIgnoreCase(puzzleType))
            .collect(Collectors.toList());
    }
    
    public List<Puzzle> getPuzzlesByDifficulty(String puzzleType, String difficulty) {
        return gameData.getPuzzles().stream()
            .filter(p -> p.getPuzzleType().equalsIgnoreCase(puzzleType))
            .filter(p -> p.getDifficulty().equalsIgnoreCase(difficulty))
            .collect(Collectors.toList());
    }
    
    public Set<String> getAvailablePuzzleTypes() {
        return gameData.getPuzzles().stream()
            .map(Puzzle::getPuzzleType)
            .collect(Collectors.toSet());
    }
    
    // ===== HINT OPERATIONS =====
    
    public List<Hint> getHintsForPuzzle(String puzzleId) {
        return gameData.getHints().stream()
            .filter(h -> h.getPuzzleId().equals(puzzleId))
            .sorted()
            .collect(Collectors.toList());
    }
    
    // ===== USER PROGRESS OPERATIONS =====
    
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
    
    public void saveUserProgress(UserProgress progress) {
        gameData.getUserProgress().removeIf(up -> up.getUserId().equals(progress.getUserId()));
        gameData.getUserProgress().add(progress);
        
        // Update leaderboard
        updateLeaderboard(progress);
        
        saveAllData();
    }
    
    public boolean isPuzzleCompleted(String userId, String puzzleId) {
        UserProgress progress = getUserProgress(userId);
        return progress.isPuzzleCompleted(puzzleId);
    }
    
    public void completePuzzle(String userId, String puzzleId, int score) {
        UserProgress progress = getUserProgress(userId);
        progress.addCompletedPuzzle(puzzleId, score);
        progress.clearGameState(); // Clear any saved game
        saveUserProgress(progress);
    }

    // ===== CERTIFICATE OPERATIONS =====
    
    public void addCertificate(Certificate certificate) {
        gameData.getCertificates().add(certificate);
        saveAllData();
    }
    
    public List<Certificate> getUserCertificates(String userId) {
        return gameData.getCertificates().stream()
            .filter(c -> c.getUserId().equals(userId))
            .sorted((c1, c2) -> c2.getEarnedAt().compareTo(c1.getEarnedAt()))
            .collect(Collectors.toList());
    }
    
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
    
    public boolean hasCertificate(String userId, String puzzleId) {
        return gameData.getCertificates().stream()
            .anyMatch(c -> c.getUserId().equals(userId) && c.getPuzzleId().equals(puzzleId));
    }
    
    // ===== LEADERBOARD OPERATIONS =====
    
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
    
    public List<LeaderboardEntry> getLeaderboard(int limit) {
        return gameData.getLeaderboard().stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public int getUserRank(String userId) {
        List<LeaderboardEntry> leaderboard = gameData.getLeaderboard();
        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return -1;
    }
}
