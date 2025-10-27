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
<<<<<<< HEAD

=======
    /**
     * Constructs a Certificate with the specified details
     * @param certificateId the unique identifier of the certificate
     * @param userId the ID of the user who earned the certificate 
     * @param puzzleId the ID of the puzzle associated with the certificate
     * @param description a description of the certificate 
     * @param difficulty the difficulty level of the puzzle
     * @param scoreAchieved the score the user achieved
     */
>>>>>>> main
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

    // Getters
<<<<<<< HEAD
=======
    /**
     *  Gets the unique identifier of the certificate 
     * 
     * @return the certificate ID
     */
>>>>>>> main
    public String getCertificateId() { return certificateId; }
    public String getUserId() { return userId; }
    public String getPuzzleId() { return puzzleId; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public int getScoreAchieved() { return scoreAchieved; }
    public LocalDateTime getEarnedAt() { return earnedAt; }  // add this getter

    // Setters
<<<<<<< HEAD
=======
    /**
     * Sets the unique identifier of the certificate
     * 
     * @param certificateId the new certificate ID
     */
>>>>>>> main
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setPuzzleId(String puzzleId) { this.puzzleId = puzzleId; }
    public void setDescription(String description) { this.description = description; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setScoreAchieved(int scoreAchieved) { this.scoreAchieved = scoreAchieved; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; } // add this setter


    @Override
    public String toString() {
        return String.format("Certificate[%s] %s - %s (%s)", 
            certificateId, description, difficulty, earnedAt);
    }
}
