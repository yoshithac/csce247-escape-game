package com.model;
import java.time.LocalDateTime;

/*
 * This class represents a Certificate in the escape game application
 * @author We're Getting an A
 */
public class Certificate {
    private String certificateId;
    private String certUserId;
    private String description;
    public LocalDateTime earnDate;
    private DifficultyLevel difficulty;

    /**
     * Constructor for Certificate
     * @param certificateId The unique ID of the certificate
     * @param certUserId The ID of the user who earned the certificate
     * @param description A description of the certificate
     * @param earnDate The date and time when the certificate was earned
     * @param difficulty The difficulty level of the puzzle associated with the certificate
     */
    public Certificate(String certificateId, String certUserId, String description, LocalDateTime earnDate, DifficultyLevel difficulty) {
        this.certificateId = certificateId;
        this.certUserId = certUserId;
        this.description = description;
        this.earnDate = earnDate;
        this.difficulty = difficulty;
    }

    /**
     * Gets the certificate ID
     * @return The certificate ID
     */
    public String getCertificateId() {
        return certificateId;
    }

    /**
     * Sets the certificate ID
     * @param certificateId The certificate ID to set
     */
    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    /**
     * Gets the user ID associated with the certificate
     * @return The user ID
     */
    public String getCertUserId() {
        return certUserId;
    }

    /**
     * Sets the user ID associated with the certificate
     * @param certUserId The user ID to set
     */
    public void setCertUserId(String certUserId) {
        this.certUserId = certUserId;
    }

    /**
     * Gets the description of the certificate
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the certificate
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the date and time when the certificate was earned
     * @return The earn date
     */
    public LocalDateTime getEarnDate() {
        return earnDate;
    }

    /**
     * Sets the date and time when the certificate was earned
     * @param earnDate The earn date to set
     */
    public void setEarnDate(LocalDateTime earnDate) {
        this.earnDate = earnDate;
    }

    /**
     * Gets the difficulty level of the puzzle associated with the certificate
     * @return The difficulty level
     */
    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty level of the puzzle associated with the certificate
     * @param difficulty The difficulty level to set
     */
    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Certificate ID: " + certificateId + "\n"
                + "User ID: " + certUserId + "\n"
                + "Description: " + description + "\n"
                + "Earned On: " + (earnDate != null ? earnDate.format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy, h:mm a")) : "N/A") + "\n"
                + "Difficulty: " + (difficulty != null ? difficulty.name() : "N/A");
    }
}
