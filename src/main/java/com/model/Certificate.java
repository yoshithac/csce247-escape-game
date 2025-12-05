package com.model;
import java.time.LocalDateTime;

/**
 * Certificate entity for puzzle completion achievements
 */
public class Certificate {
    private String certificateId;
    private String userId;
    private String puzzleId;
    private String description;
    private String difficulty;
    private int scoreAchieved;
    private int completionTimeSeconds;  // Time to complete session in seconds
    private LocalDateTime earnedAt;

    public Certificate() {
        this.earnedAt = LocalDateTime.now();
        this.completionTimeSeconds = 0;
    }

    public Certificate(String certificateId, String userId, String puzzleId,
                      String description, String difficulty, int scoreAchieved) {
        this();
        this.certificateId = certificateId;
        this.userId = userId;
        this.puzzleId = puzzleId;
        this.description = description;
        this.difficulty = difficulty;
        this.scoreAchieved = scoreAchieved;
    }

    /**
     * Gets the unique identifier of the certificate.
     *
     * @return the certificate ID
     */
    public String getCertificateId() { return certificateId; }

    /**
     * Gets the ID of the user who earned the certificate.
     *
     * @return the user ID
     */
    public String getUserId() { return userId; }

    /**
     * Gets the ID of the puzzle associated with this certificate.
     *
     * @return the puzzle ID
     */
    public String getPuzzleId() { return puzzleId; }

    /**
     * Gets the description of the certificate or achievement.
     *
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * Gets the difficulty level of the puzzle.
     *
     * @return the difficulty level
     */
    public String getDifficulty() { return difficulty; }

    /**
     * Gets the score the user achieved to earn this certificate.
     *
     * @return the achieved score
     */
    public int getScoreAchieved() { return scoreAchieved; }

    /**
     * Gets the time taken to complete the session in seconds.
     *
     * @return the completion time in seconds
     */
    public int getCompletionTimeSeconds() { return completionTimeSeconds; }

    /**
     * Gets the timestamp when the certificate was earned.
     *
     * @return the date and time the certificate was earned
     */
    public LocalDateTime getEarnedAt() { return earnedAt; }

    // Setters

    /**
     * Sets the unique identifier of the certificate.
     *
     * @param certificateId the new certificate ID
     */
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    /**
     * Sets the ID of the user who earned the certificate.
     *
     * @param userId the user ID
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Sets the ID of the puzzle associated with the certificate.
     *
     * @param puzzleId the puzzle ID
     */
    public void setPuzzleId(String puzzleId) { this.puzzleId = puzzleId; }

    /**
     * Sets the description of the certificate.
     *
     * @param description the new description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Sets the difficulty level of the puzzle.
     *
     * @param difficulty the new difficulty level
     */
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    /**
     * Sets the score the user achieved.
     *
     * @param scoreAchieved the new achieved score
     */
    public void setScoreAchieved(int scoreAchieved) { this.scoreAchieved = scoreAchieved; }

    /**
     * Sets the time taken to complete the session in seconds.
     *
     * @param completionTimeSeconds the completion time in seconds
     */
    public void setCompletionTimeSeconds(int completionTimeSeconds) { this.completionTimeSeconds = completionTimeSeconds; }

    /**
     * Sets the timestamp when the certificate was earned.
     *
     * @param earnedAt the new timestamp
     */
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }

    /**
     * Returns a string representation of the certificate,
     * including its ID, description, difficulty, and timestamp.
     *
     * @return a formatted string describing the certificate
     */
    @Override
    public String toString() {
        return String.format("Certificate[%s] %s - %s (%s)", 
            certificateId, description, difficulty, earnedAt);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Check if this is a session certificate (vs legacy puzzle certificate)
     * @return true if this is a session-based certificate
     */
    public boolean isSessionCertificate() {
        return puzzleId != null && puzzleId.startsWith("SESSION_");
    }
    
    /**
     * Get formatted completion time as "M:SS"
     * @return Formatted time string
     */
    public String getFormattedTime() {
        if (completionTimeSeconds <= 0) return "--:--";
        int minutes = completionTimeSeconds / 60;
        int seconds = completionTimeSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}