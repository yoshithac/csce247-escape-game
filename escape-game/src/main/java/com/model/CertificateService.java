package com.model;

import java.util.*;

/**
 * Service for managing puzzle completion certificates
 * Handles certificate awarding, retrieval, and statistics
 */
public class CertificateService {
    private final GameDataFacade dataFacade;
    
    public CertificateService() {
        this.dataFacade = GameDataFacade.getInstance();
    }
    
    /**
     * Award a certificate to user for completing a puzzle
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
}
