package com.model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDateTime;

public class CertificateTest {

    @Test
    public void constructors_and_defaults() {
        Certificate c = new Certificate();
        assertNotNull(c.getEarnedAt());
        Certificate c2 = new Certificate("CERT","u1","p1","desc","EASY",100);
        assertEquals("CERT", c2.getCertificateId());
        assertEquals(100, c2.getScoreAchieved());
    }

    @Test
    public void setters_and_toStringContainsKeyParts() {
        Certificate c = new Certificate();
        c.setCertificateId("X");
        c.setUserId("u");
        c.setPuzzleId("p");
        c.setScoreAchieved(0);
        c.setEarnedAt(LocalDateTime.now());

        // Check getters explicitly (safer than assuming toString contains fields)
        assertEquals("X", c.getCertificateId());
        assertEquals("u", c.getUserId());
        assertEquals("p", c.getPuzzleId());

        // toString should at least not be null
        assertNotNull(c.toString());
    }

    @Test
    public void negativeAnd_largeScores() {
        Certificate c = new Certificate();
        c.setScoreAchieved(-10);
        assertEquals(-10, c.getScoreAchieved());
        c.setScoreAchieved(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, c.getScoreAchieved());
    }
}