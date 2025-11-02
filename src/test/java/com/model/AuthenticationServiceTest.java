package com.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * AuthenticationService tests.
 * Uses reflection to instantiate GameDataFacade (private constructor) and inject it
 * into the static singleton field so tests can run with a fresh facade.
 */
public class AuthenticationServiceTest {

    @Before
    public void injectFacadeAndSeedUser() throws Exception {
        // Instantiate the real GameDataFacade via its private no-arg constructor
        Constructor<GameDataFacade> ctor = GameDataFacade.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        GameDataFacade fresh = ctor.newInstance();

        // Inject into the static field
        Field f = GameDataFacade.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, fresh);

        // Seed a known user via the public AuthenticationService.register (so facade is populated)
        AuthenticationService seedSvc = new AuthenticationService();
        // ignore return — if registration fails due to validation rules, tests below use register otherwise
        seedSvc.register("u0001", "pass", "Alice", "A", "alice@example.com");
    }

    @Test
    public void login_logout_happyPath() {
        AuthenticationService svc = new AuthenticationService();
        assertFalse(svc.isLoggedIn());
        assertNull(svc.getCurrentUser());

        boolean ok = svc.login("u0001","pass");
        assertTrue(ok);
        assertTrue(svc.isLoggedIn());
        User u = svc.getCurrentUser();
        assertNotNull(u);
        assertEquals("u0001", u.getUserId());

        svc.logout();
        assertFalse(svc.isLoggedIn());
        assertNull(svc.getCurrentUser());
    }

    @Test
    public void login_wrongPassword_unknownUser() {
        AuthenticationService svc = new AuthenticationService();
        assertFalse(svc.login("u0001","bad"));
        assertFalse(svc.isLoggedIn());
        assertFalse(svc.login("doesNotExist","pw"));
    }

    @Test
    public void register_valid_and_duplicate() {
        AuthenticationService svc = new AuthenticationService();
        boolean added = svc.register("u1000","abcd","Bob","B","bob@example.com");
        assertTrue(added);
        // duplicate should fail
        assertFalse(svc.register("u1000","abcd","Bob","B","bob@example.com"));
    }

    @Test
    public void register_invalidInputs() {
        AuthenticationService svc = new AuthenticationService();
        assertFalse(svc.register("a", "abcd","F","L","e@x.com"));
        assertFalse(svc.register("u2000","1","F","L","e@x.com"));
        assertFalse(svc.register("", "", "","", ""));
        assertFalse(svc.register(null, null, null, null, null));
    }

    @Test
    public void currentUser_remainsSame_onFailedLoginAttempt() {
        AuthenticationService svc = new AuthenticationService();
        assertTrue(svc.login("u0001","pass"));
        User before = svc.getCurrentUser();
        assertFalse(svc.login("u0001","wrong"));
        assertEquals(before, svc.getCurrentUser());
    }
}