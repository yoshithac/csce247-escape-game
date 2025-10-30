package test.java.com.model;

import org.junit.Before;
import org.junit.Test;

import com.model.ConsoleView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class ConsoleViewTest {

    private ConsoleView consoleView;
    private ByteArrayOutputStream outContent;

    @Before
    public void setup() {
        Scanner sc = new Scanner("");
        consoleView = new ConsoleView(sc);

        // capture System.out
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void showMessagePrintsToConsole() {
        consoleView.showMessage("Hello Test");
        String printed = outContent.toString();
        assertTrue(printed.contains("Hello Test"));
    }

    @Test
    public void getUserInputReadsFromScanner() {
        Scanner sc = new Scanner("line1\n");
        ConsoleView v = new ConsoleView(sc);
        String input = v.getUserInput("Prompt: ");
        assertEquals("line1", input);
    }
    @Test
public void getUserInputWhenScannerHasNoLineReturnsEmptyString() {
    Scanner sc = new Scanner("");
    ConsoleView v = new ConsoleView(sc);
    String input = v.getUserInput("Prompt: ");
    assertTrue(input == null || input.isEmpty());
}

@Test
public void showMessageHandlesNullWithoutException() {
    ConsoleView v = new ConsoleView(new Scanner(""));
    v.showMessage(null); // should not throw NPE
    String printed = outContent.toString();
    // either nothing printed or some safe fallback; ensure no NPE occurred
    assertNotNull(printed);
}
}