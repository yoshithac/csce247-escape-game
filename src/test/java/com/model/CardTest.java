package test.java.com.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void testGettersSettersAndToString() {
        Card c = new Card();
        c.setId("C1");
        c.setValue("🎲");
        c.setName("Dice");

        assertEquals("C1", c.getId());
        assertEquals("🎲", c.getValue());
        assertEquals("Dice", c.getName());

        String s = c.toString();
        assertTrue(s.contains("C1"));
        assertTrue(s.contains("Dice"));
        assertTrue(s.contains("🎲"));
    }
    @Test
public void cardToStringHandlesNullFields() {
    Card c = new Card();
    c.setId(null);
    c.setName(null);
    c.setValue(null);
    String s = c.toString();
    assertNotNull(s); // must not throw NPE
}
}