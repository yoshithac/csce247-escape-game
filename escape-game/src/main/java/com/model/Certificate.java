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
    private LocalDateTime earnDate;
    private DifficultyLevel difficulty;

    public Certificate(String certificateId, String certUserId, String description, LocalDateTime earnDate, DifficultyLevel difficulty) {
        this.certificateId = certificateId;
        this.certUserId = certUserId;
        this.description = description;
        this.earnDate = earnDate;
        this.difficulty = difficulty;
    }

    public String getCertificatId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertUserId() {
        return certUserId;
    }

    public void setCertUserId(String certUserId) {
        this.certUserId = certUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getEarnDate() {
        return earnDate;
    }

    public void setEarnDate(LocalDateTime earnDate) {
        this.earnDate = earnDate;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }
    
    @Override
    public String toString() {
        return "Certificate{" +
                "certificateId='" + certificateId + '\'' +
                ", certUserId='" + certUserId + '\'' +
                ", description='" + description + '\'' +
                ", earnDate=" + earnDate +
                ", difficulty=" + difficulty +
                '}';
    }
}
