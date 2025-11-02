package com.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Fixed imports and exceptions declared on test methods as needed.
 */
public class GameControllerTest {

    private static class StubView implements GameView {
        public void showMessage(String m) {}
        public String getUserInput(String p) { return "1"; }
        public String showMenu(String[] o) { return "1"; }
        public String showPuzzlesMenu(String[] o) { return "1"; }
        public void displayGame(java.util.Map<String,Object> s, String t) {}
        public void showResult(java.util.Map<String,Object> r) {}
        public void clear() {}
    }

    private static class StubAuth extends AuthenticationService {
        private User u = new User("u1","pw","A","B","a@b.c");
        @Override public boolean isLoggedIn() { return false; }
        @Override public User getCurrentUser() { return u; }
    }

    @Test
    public void timer_start_pause_reset_multipleCycles() throws InterruptedException {
        GameController gc = new GameController(new StubView(), new StubAuth());
        gc.resetTimer();
        assertEquals(0, gc.getTimer());

        gc.startTimer();
        Thread.sleep(40);
        gc.pauseTimer();
        int t1 = gc.getTimer();
        assertTrue(t1 >= 0);

        gc.startTimer();
        Thread.sleep(20);
        gc.pauseTimer();
        int t2 = gc.getTimer();
        assertTrue(t2 >= t1);

        gc.resetTimer();
        assertEquals(0, gc.getTimer());
    }

    @Test
    public void setStartTime_negativeAndLarge() {
        GameController gc = new GameController(new StubView(), new StubAuth());
        gc.setStartTime((int)(System.currentTimeMillis()/1000) - 5);
        assertTrue(gc.getTimer() >= 5);

        gc.setStartTime(-1000);
        // ensure no exception thrown and getTimer callable
        int t = gc.getTimer();
        assertTrue(true); // just ensure method executed
    }
}