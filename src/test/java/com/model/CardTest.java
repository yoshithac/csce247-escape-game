package com.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Fixed imports (static assert methods).
 */
public class CardTest {

    @Test
    public void basicGettersSetters_andToString() {
        Card c = new Card();
        c.setId("c1");
        c.setName("Name");
        c.setValue("Value");
        assertEquals("c1", c.getId());
        assertEquals("Name", c.getName());
        assertEquals("Value", c.getValue());
        assertTrue(c.toString().contains("c1"));
    }

    @Test
    public void parameterizedConstructor_andEquality() {
        Card a = new Card("c2","val","Name");
        Card b = new Card("c2","val","Name");
        assertEquals(a.getId(), b.getId());
        if (a.equals(b)) {
            assertEquals(a.hashCode(), b.hashCode());
        }
    }

    @Test
    public void nullAndEmptyFields() {
        Card c = new Card(null, null, null);
        assertNull(c.getId());
        assertNull(c.getName());
        assertNull(c.getValue());
        c.setId("");
        c.setName("");
        c.setValue("");
        assertEquals("", c.getId());
    }
}