package test.java.com.model;

import org.junit.Test;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class CertificateTest {

    @Test
    public void testConstructorAndGettersSetters() {
        Certificate cert = new Certificate("CERT_1", "userA", "PUZ_1", "Completed puzzle", "HARD", 95);

        assertEquals("CERT_1", cert.getCertificateId());
        assertEquals("userA", cert.getUserId());
        assertEquals("PUZ_1", cert.getPuzzleId());
        assertEquals("Completed puzzle", cert.getDescription());
        assertEquals("HARD", cert.getDifficulty());
        assertEquals(95, cert.getScoreAchieved());
        assertNotNull(cert.getEarnedAt());

        // test setters
        cert.setDescription("New desc");
        cert.setDifficulty("EASY");
        cert.setScoreAchieved(50);
        LocalDateTime now = LocalDateTime.now();
        cert.setEarnedAt(now);

        assertEquals("New desc", cert.getDescription());
        assertEquals("EASY", cert.getDifficulty());
        assertEquals(50, cert.getScoreAchieved());
        assertEquals(now, cert.getEarnedAt());

        String t = cert.toString();
        assertTrue(t.contains("CERT_1"));
        assertTrue(t.contains("EASY"));
    }
    @Test
public void certificateToStringHandlesNullFields() {
    Certificate cert = new Certificate();
    cert.setCertificateId(null);
    cert.setDifficulty(null);
    cert.setDescription(null);
    String s = cert.toString();
    assertNotNull(s);
}
}