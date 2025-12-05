package com.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing puzzle/session completion certificates
 * Handles certificate awarding, retrieval, and statistics
 * 
 * Supports both:
 * - Legacy per-puzzle certificates
 * - Session certificates (awarded when all 5 puzzles completed)
 */
public class CertificateService {
    private final GameDataFacade dataFacade;
    
    public CertificateService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    // ==================== SESSION CERTIFICATE METHODS ====================
    
    /**
     * Award a certificate for completing a game session (all 5 puzzles)
     * 
     * @param userId User ID
     * @param difficulty Session difficulty (EASY, MEDIUM, HARD)
     * @param totalScore Combined score from all 5 puzzles
     * @param completionTimeSeconds Time taken to complete session
     * @return The created Certificate
     */
    public Certificate awardSessionCertificate(String userId, String difficulty, 
                                                int totalScore, int completionTimeSeconds) {
        String certId = "CERT_" + userId + "_SESSION_" + difficulty + "_" + System.currentTimeMillis();
        String puzzleId = "SESSION_" + difficulty;
        String description = String.format("Escaped Haunted Manor on %s difficulty in %s", 
                                          difficulty, formatTime(completionTimeSeconds));
        
        Certificate cert = new Certificate(
            certId,
            userId,
            puzzleId,
            description,
            difficulty,
            totalScore
        );
        
        // Set completion time for session certificate
        cert.setCompletionTimeSeconds(completionTimeSeconds);
        
        dataFacade.addCertificate(cert);
        System.out.println("✓ Session certificate awarded: " + difficulty + " - " + totalScore + " pts");
        return cert;
    }
    
    // ==================== STATISTICS METHODS ====================
    
    /**
     * Get best (fastest) completion time for a difficulty
     * @param userId User ID
     * @param difficulty Difficulty level
     * @return Best time in seconds, or -1 if no completions
     */
    public int getBestTime(String userId, String difficulty) {
        // For now, return -1 as we don't store time in basic Certificate
        // This can be enhanced when Certificate gains completionTimeSeconds field
        return -1;
    }
    
    /**
     * Get best (highest) score for a difficulty
     * @param userId User ID
     * @param difficulty Difficulty level
     * @return Best score, or 0 if no completions
     */
    public int getBestScore(String userId, String difficulty) {
        String puzzleId = "SESSION_" + difficulty;
        return getUserCertificates(userId).stream()
            .filter(c -> puzzleId.equals(c.getPuzzleId()))
            .mapToInt(Certificate::getScoreAchieved)
            .max()
            .orElse(0);
    }
    
    /**
     * Get total score across all certificates
     * @param userId User ID
     * @return Sum of all certificate scores
     */
    public int getTotalCertificateScore(String userId) {
        return getUserCertificates(userId).stream()
            .mapToInt(Certificate::getScoreAchieved)
            .sum();
    }
    
    /**
     * Get comprehensive user certificate stats
     * @param userId User ID
     * @return Map containing various statistics
     */
    public Map<String, Object> getUserCertificateStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Integer> counts = getCertificateStats(userId);
        
        stats.put("easyCount", counts.getOrDefault("EASY", 0));
        stats.put("mediumCount", counts.getOrDefault("MEDIUM", 0));
        stats.put("hardCount", counts.getOrDefault("HARD", 0));
        stats.put("totalCount", getCertificateCount(userId));
        stats.put("totalScore", getTotalCertificateScore(userId));
        
        return stats;
    }
    
    /**
     * Get leaderboard comparison data
     * @param userId User ID
     * @return Map containing rank, percentile, and comparison data
     */
    public Map<String, Object> getLeaderboardComparison(String userId) {
        Map<String, Object> comparison = new HashMap<>();
        
        int rank = dataFacade.getUserRank(userId);
        int totalPlayers = dataFacade.getFullLeaderboard().size();
        
        comparison.put("rank", rank);
        comparison.put("totalPlayers", totalPlayers);
        comparison.put("rankDisplay", rank > 0 ? "#" + rank + " of " + totalPlayers : "Not ranked");
        
        if (rank > 0 && totalPlayers > 0) {
            int percentile = (int) Math.round(((double)(totalPlayers - rank) / totalPlayers) * 100);
            comparison.put("percentile", percentile);
            comparison.put("percentileDisplay", "Top " + (100 - percentile) + "%");
        } else {
            comparison.put("percentile", -1);
            comparison.put("percentileDisplay", "N/A");
        }
        
        return comparison;
    }
    
    // ==================== LEGACY METHODS ====================
    
    /**
     * Award a certificate to user for completing a puzzle (legacy)
     * @param userId User ID
     * @param puzzle Puzzle that was completed
     * @param score Score achieved
     * @return The created Certificate
     */
    public Certificate awardCertificate(String userId, Puzzle puzzle, int score) {
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
        return cert;
    }
    
    /**
     * Get all certificates earned by user
     * @param userId User ID
     * @return List of user's certificates
     */
    public List<Certificate> getUserCertificates(String userId) {
        return dataFacade.getUserCertificates(userId);
    }
    
    /**
     * Get certificate statistics for user (by difficulty)
     * @param userId User ID
     * @return Map with counts: EASY, MEDIUM, HARD
     */
    public Map<String, Integer> getCertificateStats(String userId) {
        return dataFacade.getCertificateStats(userId);
    }
    
    /**
     * Check if user has earned certificate for specific puzzle
     * @param userId User ID
     * @param puzzleId Puzzle ID
     * @return true if certificate exists
     */
    public boolean hasCertificate(String userId, String puzzleId) {
        return dataFacade.hasCertificate(userId, puzzleId);
    }
    
    /**
     * Get total certificate count for user
     * @param userId User ID
     * @return Total number of certificates
     */
    public int getCertificateCount(String userId) {
        return getUserCertificates(userId).size();
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Format seconds to M:SS string
     */
    public static String formatTime(int seconds) {
        if (seconds < 0) return "--:--";
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }
}
