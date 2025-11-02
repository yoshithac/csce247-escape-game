package com.model;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for Certificate entity
 * Each test method contains exactly one assertion
 */
public class CertificateTest {
    
    private Certificate certificate;
    
    @Before
    public void setUp() {
        certificate = new Certificate("CERT_001", "user1", "puzzle1", 
                                      "Completed Test Puzzle", "MEDIUM", 100);
    }
    
    @Test
    public void testConstructorSetsCertificateId() {
        assertEquals("CERT_001", certificate.getCertificateId());
    }
    
    @Test
    public void testConstructorSetsUserId() {
        assertEquals("user1", certificate.getUserId());
    }
    
    @Test
    public void testConstructorSetsPuzzleId() {
        assertEquals("puzzle1", certificate.getPuzzleId());
    }
    
    @Test
    public void testConstructorSetsDescription() {
        assertEquals("Completed Test Puzzle", certificate.getDescription());
    }
    
    @Test
    public void testConstructorSetsDifficulty() {
        assertEquals("MEDIUM", certificate.getDifficulty());
    }
    
    @Test
    public void testConstructorSetsScore() {
        assertEquals(100, certificate.getScoreAchieved());
    }
    
    @Test
    public void testConstructorSetsEarnedAt() {
        assertNotNull(certificate.getEarnedAt());
    }
    
    @Test
    public void testDefaultConstructorSetsEarnedAt() {
        Certificate cert = new Certificate();
        assertNotNull(cert.getEarnedAt());
    }
    
    @Test
    public void testSetCertificateId() {
        certificate.setCertificateId("CERT_002");
        assertEquals("CERT_002", certificate.getCertificateId());
    }
    
    @Test
    public void testSetUserId() {
        certificate.setUserId("user2");
        assertEquals("user2", certificate.getUserId());
    }
    
    @Test
    public void testSetPuzzleId() {
        certificate.setPuzzleId("puzzle2");
        assertEquals("puzzle2", certificate.getPuzzleId());
    }
    
    @Test
    public void testSetDescription() {
        certificate.setDescription("New Description");
        assertEquals("New Description", certificate.getDescription());
    }
    
    @Test
    public void testSetDifficulty() {
        certificate.setDifficulty("HARD");
        assertEquals("HARD", certificate.getDifficulty());
    }
    
    @Test
    public void testSetScoreAchieved() {
        certificate.setScoreAchieved(150);
        assertEquals(150, certificate.getScoreAchieved());
    }
    
    @Test
    public void testSetEarnedAt() {
        LocalDateTime testTime = LocalDateTime.now();
        certificate.setEarnedAt(testTime);
        assertEquals(testTime, certificate.getEarnedAt());
    }
    
    @Test
    public void testToStringContainsCertificateId() {
        assertTrue(certificate.toString().contains("CERT_001"));
    }
    /* 
    @Test
    public void testToStringContainsUserId() {
        assertTrue(certificate.toString().contains("user1"));
    }
    */
    @Test
    public void testToStringContainsDifficulty() {
        assertTrue(certificate.toString().contains("MEDIUM"));
    }
}
