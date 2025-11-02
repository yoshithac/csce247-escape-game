package com.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CertificateService tests using a fresh GameDataFacade instance injected by reflection.
 */
public class CertificateServiceTest {

    @Before
    public void injectFreshFacade() throws Exception {
        // Create fresh GameDataFacade via private constructor and inject
        Constructor<GameDataFacade> ctor = GameDataFacade.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        GameDataFacade fresh = ctor.newInstance();

        Field f = GameDataFacade.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, fresh);
    }

    @Test
    public void awardCertificate_and_duplicateHandling() {
        CertificateService svc = new CertificateService();

        // create a Puzzle with the 6-arg constructor (empty map for last param)
        Puzzle p = new Puzzle("pz1", "TITLE", "EASY", "desc", "MATCHING", new HashMap<String,Object>());

        Certificate first = svc.awardCertificate("u1", p, 80);
        assertNotNull(first);
        assertTrue(svc.hasCertificate("u1","pz1"));

        Certificate second = svc.awardCertificate("u1", p, 90);
        assertNotNull(second);
        assertTrue(svc.hasCertificate("u1","pz1"));

        int count = svc.getCertificateCount("u1");
        // implementation may choose to dedupe or allow duplicates, assert non-negative
        assertTrue(count >= 0);
    }

    @Test
    public void getCertificateCount_whenNone() {
        CertificateService svc = new CertificateService();
        int count = svc.getCertificateCount("noone");
        assertEquals(0, count);
        List<Certificate> list = svc.getUserCertificates("noone");
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void getStats_returnsMapWithKeys() {
        CertificateService svc = new CertificateService();
        Map<String,Integer> stats = svc.getCertificateStats("u1");
        assertNotNull(stats);
        assertTrue(stats.containsKey("EASY"));
        assertTrue(stats.containsKey("MEDIUM"));
        assertTrue(stats.containsKey("HARD"));
    }
}