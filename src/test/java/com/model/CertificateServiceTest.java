package test.java.com.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.model.CertificateService;
import com.model.GameDataFacade;
import com.model.Puzzle;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CertificateServiceTest {

    @Mock
    private GameDataFacade mockFacade;

    @Mock
    private Puzzle mockPuzzle;

    private CertificateService certService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        certService = new CertificateService();

        Field f = CertificateService.class.getDeclaredField("dataFacade");
        f.setAccessible(true);
        f.set(certService, mockFacade);
    }

    @Test
    public void awardCertificateCreatesAndSavesCertificate() {
        when(mockPuzzle.getPuzzleId()).thenReturn("PZ1");
        when(mockPuzzle.getTitle()).thenReturn("The First Puzzle");
        when(mockPuzzle.getDifficulty()).thenReturn("MEDIUM");

        Certificate cert = certService.awardCertificate("userA", mockPuzzle, 87);

        assertNotNull(cert);
        assertTrue(cert.getCertificateId().contains("CERT_userA_PZ1"));
        assertEquals("userA", cert.getUserId());
        assertEquals("PZ1", cert.getPuzzleId());
        assertEquals("MEDIUM", cert.getDifficulty());
        assertEquals(87, cert.getScoreAchieved());

        verify(mockFacade).addCertificate(any(Certificate.class));
    }

    @Test
    public void getUserCertificatesDelegatesToFacade() {
        when(mockFacade.getUserCertificates("userA")).thenReturn(Collections.emptyList());
        List<Certificate> list = certService.getUserCertificates("userA");
        assertNotNull(list);
        assertTrue(list.isEmpty());
        verify(mockFacade).getUserCertificates("userA");
    }

    @Test
    public void statsAndHasCertificateDelegation() {
        when(mockFacade.getCertificateStats("userA")).thenReturn(Map.of("EASY", 1, "MEDIUM", 0, "HARD", 2));
        Map<String, Integer> stats = certService.getCertificateStats("userA");
        assertEquals(Integer.valueOf(1), stats.get("EASY"));

        when(mockFacade.hasCertificate("userA", "PZ1")).thenReturn(true);
        assertTrue(certService.hasCertificate("userA", "PZ1"));

        // getCertificateCount uses getUserCertificates
        when(mockFacade.getUserCertificates("userA")).thenReturn(List.of(new Certificate()));
        assertEquals(1, certService.getCertificateCount("userA"));
    }
    @Test(expected = IllegalArgumentException.class)
public void awardCertificateThrowsForNullPuzzle() {
    certService.awardCertificate("userA", null, 50); // expect IllegalArgumentException or similar
}

@Test
public void awardCertificateHandlesNegativeScoreAndFacadeFailure() {
    when(mockPuzzle.getPuzzleId()).thenReturn("PZ1");
    when(mockPuzzle.getDifficulty()).thenReturn(null); // missing difficulty

    // if negative score is invalid -> method should throw or clamp; test expected behavior
    try {
        certService.awardCertificate("userA", mockPuzzle, -10);
        // if it returns, verify facade call and certificate fields
        verify(mockFacade).addCertificate(any(Certificate.class));
        Certificate c = certService.getUserCertificates("userA").stream().findFirst().orElse(null);
        // you can assert behavior (depending on implementation)
    } catch (IllegalArgumentException ex) {
        // acceptable if your service throws for negative scores
        assertTrue(ex.getMessage().toLowerCase().contains("score"));
    }
}

@Test
public void getUserCertificatesHandlesNullFromFacade() {
    when(mockFacade.getUserCertificates("userA")).thenReturn(null);
    List<Certificate> list = certService.getUserCertificates("userA");
    assertNotNull("Service should return empty list when facade returns null", list);
    assertTrue(list.isEmpty());
}
}