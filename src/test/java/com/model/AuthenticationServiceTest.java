package test.java.com.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.model.AuthenticationService;
import com.model.GameDataFacade;
import com.model.User;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private GameDataFacade mockFacade;

    @Mock
    private User mockUser;

    private AuthenticationService authService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        // create real service then inject mock facade via reflection (dataFacade is private final)
        authService = new AuthenticationService();

        // inject mockFacade into private final field dataFacade
        Field f = AuthenticationService.class.getDeclaredField("dataFacade");
        f.setAccessible(true);
        f.set(authService, mockFacade);
    }

    @Test
    public void loginSuccessSetsCurrentUserAndReturnsTrue() {
        when(mockFacade.getUser("u123")).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn("pass");

        boolean ok = authService.login("u123", "pass");
        assertTrue(ok);
        assertTrue(authService.isLoggedIn());
        assertEquals(mockUser, authService.getCurrentUser());

        // ensure updateUser called (service updates lastLogin then saves)
        verify(mockFacade, atLeastOnce()).updateUser(any(User.class));
    }

    @Test
    public void loginFailureWrongPasswordReturnsFalse() {
        when(mockFacade.getUser("u123")).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn("secret");

        boolean ok = authService.login("u123", "wrong");
        assertFalse(ok);
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentUser());
    }

    @Test
    public void registerValidationAndSuccess() {
        // invalid userId null or wrong length
        assertFalse(authService.register(null, "abcd", "A", "B", "a@b.com"));
        assertFalse(authService.register("abc", "abcd", "A", "B", "a@b.com")); // not length 5

        // short password
        assertFalse(authService.register("ABCDE", "abc", "A", "B", "a@b.com"));

        // existing user id
        when(mockFacade.userIdExists("ABCDE")).thenReturn(true);
        assertFalse(authService.register("ABCDE", "abcd", "A", "B", "a@b.com"));

        // fresh id -> should call addUser and return whatever facade returns
        when(mockFacade.userIdExists("NEW01")).thenReturn(false);
        when(mockFacade.addUser(any(User.class))).thenReturn(true);

        boolean created = authService.register("NEW01", "abcd", "First", "Last", "e@mail.com");
        assertTrue(created);
        verify(mockFacade, times(1)).addUser(any(User.class));
    }
    @Test
public void loginHandlesMissingUserAndNullInputs() throws Exception {
    when(mockFacade.getUser("uX")).thenReturn(Optional.empty());
    assertFalse(authService.login("uX", "pw")); // missing user

    // null userId or password should return false (service should guard)
    assertFalse(authService.login(null, "pw"));
    assertFalse(authService.login("uX", null));
}

@Test
public void loginHandlesFacadeExceptionGracefully() throws Exception {
    when(mockFacade.getUser("uX")).thenThrow(new RuntimeException("DB fail"));
    try {
        boolean ok = authService.login("uX", "pw");
        // decide expected behavior: either false or rethrow; pick one consistent expectation
        assertFalse(ok);
    } catch (RuntimeException ex) {
        // If your implementation rethrows, this is acceptable; assert message
        assertTrue(ex.getMessage().contains("DB fail"));
    }
}@Test
public void registerHandlesFacadeAddUserFailure() throws Exception {
    when(mockFacade.userIdExists("NEW01")).thenReturn(false);
    when(mockFacade.addUser(any(User.class))).thenReturn(false); // persistence failure

    boolean created = authService.register("NEW01", "abcd", "F", "L", "e@mail.com");
    assertFalse(created);
    verify(mockFacade).addUser(any(User.class));
}

@Test
public void registerRejectsInvalidEmailsAndEdgePasswords() {
    // invalid email format (if you validate)
    assertFalse(authService.register("ABCDE", "abcd", "F", "L", "not-an-email"));

    // password exactly min length — if min=4 this should pass; if not, change accordingly
    assertTrue(authService.register("USR01", "1234", "F", "L", "a@b.com"));
}
}
