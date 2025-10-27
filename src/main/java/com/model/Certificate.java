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
    private LocalDateTime earnedAt;  // add this field

    public Certificate() {
        this.earnedAt = LocalDateTime.now();
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
}