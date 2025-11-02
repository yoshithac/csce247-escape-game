package com.model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Fixed imports (Queue, LinkedList, JUnit asserts)
 */
public class AuthControllerTest {

    private static class StubView implements GameView {
        Queue<String> inputs = new LinkedList<>();
        String lastMessage = "";

        public StubView(String... inputs) { for (String s : inputs) this.inputs.add(s); }

        public void showMessage(String message) { lastMessage = message; }
        public String getUserInput(String prompt) { return inputs.isEmpty()? "" : inputs.poll(); }
        public String showMenu(String[] options) { return inputs.isEmpty()? "3" : inputs.poll(); }
        public String showPuzzlesMenu(String[] options) { return showMenu(options); }
        public void displayGame(java.util.Map<String,Object> s, String t) {}
        public void showResult(java.util.Map<String,Object> r) {}
        public void clear() {}
    }

    private static class StubAuthService extends AuthenticationService {
        private User current;
        @Override
        public boolean login(String userId, String password) {
            if ("u1111".equals(userId) && "pw".equals(password)) {
                current = new User(userId,password,"Fn","Ln","e@x.com");
                return true;
            }
            return false;
        }
        @Override public User getCurrentUser() { return current; }
        @Override public boolean isLoggedIn() { return current != null; }
        @Override public void logout() { current = null; }
        @Override public boolean register(String userId, String password, String firstName, String lastName, String email) {
            if (userId==null || password==null) return false;
            if (userId.length()<2 || password.length()<2) return false;
            return true;
        }
    }

    @Test
    public void loginFlow_success() {
        StubView v = new StubView("1","u1111","pw");
        StubAuthService a = new StubAuthService();
        AuthController c = new AuthController(v,a);
        assertTrue(c.showAuthMenu());
        assertTrue(a.isLoggedIn());
        assertNotNull(a.getCurrentUser());
    }

    @Test
    public void loginFlow_failure_then_exit() {
        StubView v = new StubView("1","u1111","wrong","3");
        StubAuthService a = new StubAuthService();
        AuthController c = new AuthController(v,a);
        assertFalse(c.showAuthMenu());
        assertFalse(a.isLoggedIn());
    }

    @Test
    public void registerFlow_valid_and_invalid() {
        StubView v = new StubView("2","newu","pw","First","Last","email@e.com","3");
        StubAuthService a = new StubAuthService();
        AuthController c = new AuthController(v,a);
        c.showAuthMenu();

        StubView v2 = new StubView("2","a","1","F","L","e","3");
        AuthController c2 = new AuthController(v2,a);
        c2.showAuthMenu();
    }

    @Test
    public void invalidMenuInput_handledGracefully() {
        StubView v = new StubView("foo","42","3");
        StubAuthService a = new StubAuthService();
        AuthController c = new AuthController(v,a);
        assertFalse(c.showAuthMenu());
    }

    @Test
    public void logout_clearsSession() {
        StubView v = new StubView();
        StubAuthService a = new StubAuthService();
        AuthController c = new AuthController(v,a);
        assertTrue(a.login("u1111","pw"));
        assertTrue(a.isLoggedIn());
        c.logout();
        assertFalse(a.isLoggedIn());
    }
}