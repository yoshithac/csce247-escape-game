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
}
