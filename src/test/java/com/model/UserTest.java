package com.model;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for the User class.
 * Verifies constructors, getters/setters, role checks, timestamps and utility methods.
 */
public class UserTest {

    /**
     * Tests the default constructor.
     * Ensures createdAt is initialized and default role is USER.
     */
    @Test
    public void testDefaultConstructor_initializesCreatedAtAndDefaultRole() {
        User u = new User();

        assertNotNull("User instance should not be null", u);
        assertNotNull("createdAt should be initialized by default constructor", u.getCreatedAt());
        assertEquals("Default role should be USER", "USER", u.getRole());
        // lastLoginAt is not set by default constructor
        assertNull("lastLoginAt should be null by default", u.getLastLoginAt());
    }

    /**
     * Tests the parameterized constructor sets basic profile fields and preserves defaults.
     */
    @Test
    public void testParameterizedConstructor_setsProfileFieldsAndDefaults() {
        User u = new User("alice01", "pw123", "Alice", "Doe", "alice@example.com");

        assertEquals("userId should match constructor value", "alice01", u.getUserId());
        assertEquals("password should match constructor value", "pw123", u.getPassword());
        assertEquals("firstName should match constructor value", "Alice", u.getFirstName());
        assertEquals("lastName should match constructor value", "Doe", u.getLastName());
        assertEquals("email should match constructor value", "alice@example.com", u.getEmail());

        // default role and createdAt come from default constructor call inside full constructor
        assertEquals("Default role should be USER", "USER", u.getRole());
        assertNotNull("createdAt should be initialized", u.getCreatedAt());
    }

    /**
     * Tests setters and getters update and return values correctly.
     */
    @Test
    public void testSettersAndGetters_updateAndReturnValues() {
        User u = new User();
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 12, 0);

        u.setUserId("bob");
        u.setPassword("secret");
        u.setFirstName("Bob");
        u.setLastName("Smith");
        u.setEmail("bob@example.com");
        u.setRole("ADMIN");
        u.setCreatedAt(now);
        u.setLastLoginAt(now);

        assertEquals("userId getter should return set value", "bob", u.getUserId());
        assertEquals("password getter should return set value", "secret", u.getPassword());
        assertEquals("firstName getter should return set value", "Bob", u.getFirstName());
        assertEquals("lastName getter should return set value", "Smith", u.getLastName());
        assertEquals("email getter should return set value", "bob@example.com", u.getEmail());
        assertEquals("role getter should return set value", "ADMIN", u.getRole());
        assertEquals("createdAt getter should return previously set value", now, u.getCreatedAt());
        assertEquals("lastLoginAt getter should return previously set value", now, u.getLastLoginAt());
    }

    /**
     * Tests isAdmin returns true only when role indicates ADMIN (case-insensitive).
     */
    @Test
    public void testIsAdmin_trueWhenAdmin_falseOtherwise() {
        User u = new User();
        u.setRole("USER");
        assertFalse("isAdmin should be false for role USER", u.isAdmin());

        u.setRole("ADMIN");
        assertTrue("isAdmin should be true for role ADMIN", u.isAdmin());

        u.setRole("admin"); // case-insensitive check
        assertTrue("isAdmin should be true for role 'admin' (case-insensitive)", u.isAdmin());
    }

    /**
     * Tests updateLastLogin sets lastLoginAt to a recent non-null timestamp.
     */
    @Test
    public void testUpdateLastLogin_setsNonNullLastLoginAt() {
        User u = new User();
        assertNull("lastLoginAt should be null before update", u.getLastLoginAt());

        u.updateLastLogin();
        assertNotNull("lastLoginAt should be non-null after updateLastLogin", u.getLastLoginAt());
        // We don't assert exact time; just ensure it is reasonably recent (not before createdAt)
        assertTrue("lastLoginAt should be equal or after createdAt", 
                   !u.getLastLoginAt().isBefore(u.getCreatedAt()));
    }

    /**
     * Tests getFullName concatenates first and last names with a space.
     */
    @Test
    public void testGetFullName_returnsConcatenatedName() {
        User u = new User();
        u.setFirstName("Jane");
        u.setLastName("Roe");

        assertEquals("getFullName should return 'First Last'", "Jane Roe", u.getFullName());
    }

    /**
     * Tests toString includes identifying user information.
     */
    @Test
    public void testToString_containsUserIdAndNameAndEmail() {
        User u = new User("u1", "p", "First", "Last", "f@l.com");
        String s = u.toString();

        assertTrue("toString should contain userId", s.contains("u1"));
        assertTrue("toString should contain first name", s.contains("First"));
        assertTrue("toString should contain last name", s.contains("Last"));
        assertTrue("toString should contain email", s.contains("f@l.com"));
    }
}
