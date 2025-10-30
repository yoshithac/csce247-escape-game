package test.java.com.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.model.AuthController;
import com.model.AuthenticationService;
import com.model.GameView;
import com.model.User;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AuthControllerTest {

    @Mock
    private GameView mockView;

    @Mock
    private AuthenticationService mockAuth;

    private AuthController controller;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new AuthController(mockView, mockAuth);
    }

    @Test
    public void showLoginSuccessfulReturnsTrue() {
        // simulate menu choice "1" (Login) then in login flow provide credentials
        when(mockView.showMenu(any(String[].class))).thenReturn("1");
        when(mockView.getUserInput("User ID: ")).thenReturn("u1");
        when(mockView.getUserInput("Password: ")).thenReturn("p1");

        when(mockAuth.login("u1", "p1")).thenReturn(true);
        User fake = mock(User.class);
        when(fake.getFullName()).thenReturn("Test User");
        when(mockAuth.getCurrentUser()).thenReturn(fake);

        // Because showAuthMenu has a loop and returns true on successful login,
        // call should return true.
        boolean result = controller.showAuthMenu();
        assertTrue(result);

        verify(mockView, atLeastOnce()).showMessage(contains("WELCOME"));
        verify(mockAuth).login("u1", "p1");
    }

    @Test
    public void showRegistrationDisplaysFeedback() {
        // choose menu option "2" (Register New User) then choose exit "3" to end loop
        when(mockView.showMenu(any(String[].class))).thenReturn("2", "3");

        // fill inputs for registration
        when(mockView.getUserInput("Choose User ID (5 chars): ")).thenReturn("ABCDE");
        when(mockView.getUserInput("Password (min 4 chars): ")).thenReturn("pw12");
        when(mockView.getUserInput("First Name: ")).thenReturn("F");
        when(mockView.getUserInput("Last Name: ")).thenReturn("L");
        when(mockView.getUserInput("Email: ")).thenReturn("e@mail.com");

        when(mockAuth.register("ABCDE", "pw12", "F", "L", "e@mail.com")).thenReturn(true);

        boolean ret = controller.showAuthMenu(); // will run one register then exit
        assertFalse(ret); // because choosing "3" returns false (exit)
        verify(mockAuth).register("ABCDE", "pw12", "F", "L", "e@mail.com");
        verify(mockView, atLeastOnce()).showMessage(contains("Registration successful"));
    }
    @Test
public void showAuthMenuHandlesInvalidMenuSelectionAndNulls() {
    when(mockView.showMenu(any(String[].class))).thenReturn(null, "3"); // null selection then exit
    boolean ret = controller.showAuthMenu();
    assertFalse(ret); // expect exit or safe handling
    verify(mockView, atLeastOnce()).showMessage(anyString());
}

@Test(expected = RuntimeException.class)
public void showAuthMenuPropagatesViewExceptionIfUnrecoverable() {
    when(mockView.showMenu(any(String[].class))).thenThrow(new RuntimeException("IO"));
    controller.showAuthMenu(); // should either catch or propagate - assert whichever your design expects
}
}