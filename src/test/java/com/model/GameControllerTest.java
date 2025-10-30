package test.java.com.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GameController covering parse/timer/calc/save/resume/view helpers.
 * - Uses reflection to inject mocks for internal services (progress/leaderboard/certificate/dataFacade).
 * - Avoids running the full interactive start() / playGame() loop.
 */
public class GameControllerTest {

    @Mock private GameView view;
    @Mock private AuthenticationService authService;

    // services to inject
    @Mock private GameProgressService progressService;
    @Mock private LeaderboardService leaderboardService;
    @Mock private CertificateService certificateService;
    @Mock private GameDataFacade dataFacade;

    // domain mocks
    @Mock private User mockUser;
    @Mock private UserProgress mockProgress;
    @Mock private Puzzle mockPuzzle;
    @Mock private PuzzleGame mockGame;

    private GameController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // default logged in user
        when(authService.isLoggedIn()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUserId()).thenReturn("u1");
        when(mockUser.getFullName()).thenReturn("Test User");

        // create controller with mocks for interactive pieces
        controller = new GameController(view, authService);

        // inject the mocked services and dataFacade via reflection (private final fields)
        setPrivateField(controller, "progressService", progressService);
        setPrivateField(controller, "leaderboardService", leaderboardService);
        setPrivateField(controller, "certificateService", certificateService);
        setPrivateField(controller, "dataFacade", dataFacade);
    }

    // small helper to set private final fields
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    // helper to call private methods
    private Object callPrivate(Object target, String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method m = target.getClass().getDeclaredMethod(methodName, paramTypes);
        m.setAccessible(true);
        return m.invoke(target, args);
    }

    @Test
    public void testParseChoice_validAndInvalid() throws Exception {
        int v1 = (int) callPrivate(controller, "parseChoice", new Class[]{String.class}, "  3 ");
        assertEquals(3, v1);

        int v2 = (int) callPrivate(controller, "parseChoice", new Class[]{String.class}, "bogus");
        assertEquals(-1, v2);

        int v3 = (int) callPrivate(controller, "parseChoice", new Class[]{String.class}, "  ");
        assertEquals(-1, v3);
    }

    @Test
    public void testTimer_startPauseGetReset_and_setStartTime() throws Exception {
        // set a fake start time 5 seconds in the past
        int nowSec = (int)(System.currentTimeMillis() / 1000);
        controller.setStartTime(nowSec - 5);
        int t1 = controller.getTimer();
        assertTrue("Timer should be >= 5", t1 >= 5);

        controller.pauseTimer();
        int paused = controller.getTimer();
        assertTrue("Paused timer should be >= 5", paused >= 5);

        controller.resetTimer();
        assertEquals(0, controller.getTimer());
    }

    @Test
    public void testCalculateScore_formula() throws Exception {
        // calculateScore expects keys "time" (long ms) and "moves" (int)
        Map<String,Object> result = new HashMap<>();
        // time 3 seconds (3000 ms), moves 5
        result.put("time", 3000L);
        result.put("moves", 5);

        int score = (int) callPrivate(controller, "calculateScore", new Class[]{Map.class}, result);
        // expected: base 100 + timeBonus (100 - 3) + moveBonus (50 - 5)
        int expected = 100 + Math.max(0, 100 - (int)(3000L/1000)) + Math.max(0, 50 - 5);
        assertEquals(expected, score);
    }

    @Test
    public void testViewProgress_printsExpectedLines() throws Exception {
        // arrange
        when(progressService.getUserProgress("u1")).thenReturn(mockProgress);
        when(mockProgress.getTotalScore()).thenReturn(250);
        Map<String,Integer> stats = new HashMap<>();
        stats.put("completed", 3);
        stats.put("totalPuzzles", 10);
        stats.put("completionPercentage", 30);
        stats.put("remaining", 7);
        when(progressService.getProgressStats("u1")).thenReturn(stats);

        // call private viewProgress()
        callPrivate(controller, "viewProgress", new Class[]{});

        // verify view interactions: should show messages containing the values
        verify(view).showMessage(contains("YOUR PROGRESS"));
        verify(view).showMessage(contains("Total Score: 250"));
        verify(view).showMessage(contains("Puzzles Completed: 3/10"));
        verify(view).showMessage(contains("Completion: 30%"));
        verify(view).showMessage(contains("Remaining: 7"));
    }

    @Test
    public void testViewLeaderboard_showsTopAndUserMarker() throws Exception {
        // prepare leaderboard entries
        LeaderboardEntry e1 = mock(LeaderboardEntry.class);
        LeaderboardEntry e2 = mock(LeaderboardEntry.class);
        when(e1.getUserId()).thenReturn("u2");
        when(e1.getUserName()).thenReturn("Alice");
        when(e1.getTotalScore()).thenReturn(500);
        when(e1.getPuzzlesCompleted()).thenReturn(10);

        when(e2.getUserId()).thenReturn("u1"); // same as current user to test marker
        when(e2.getUserName()).thenReturn("Test User");
        when(e2.getTotalScore()).thenReturn(300);
        when(e2.getPuzzlesCompleted()).thenReturn(6);

        when(leaderboardService.getTopPlayers(10)).thenReturn(Arrays.asList(e1,e2));
        when(leaderboardService.getUserRank("u1")).thenReturn(5);

        callPrivate(controller, "viewLeaderboard", new Class[]{});

        verify(view).showMessage(contains("LEADERBOARD - TOP 10"));
        // should show both entries
        verify(view).showMessage(contains("Alice"));
        verify(view).showMessage(contains("Test User"));
        // if userRank <= 10, it shouldn't show the "Your rank" extra block; but we still verify it's called
        verify(view, atLeastOnce()).showMessage(anyString());
    }

    @Test
    public void testViewCertificates_displaysCountsAndRecent() throws Exception {
        Certificate c1 = new Certificate("C1","u1","P1","Desc1","EASY",80);
        Certificate c2 = new Certificate("C2","u1","P2","Desc2","MEDIUM",90);
        when(certificateService.getUserCertificates("u1")).thenReturn(Arrays.asList(c1,c2));
        Map<String,Integer> stats = new HashMap<>();
        stats.put("EASY", 1);
        stats.put("MEDIUM", 1);
        stats.put("HARD", 0);
        when(certificateService.getCertificateStats("u1")).thenReturn(stats);

        callPrivate(controller, "viewCertificates", new Class[]{});

        verify(view).showMessage(contains("YOUR CERTIFICATES"));
        verify(view).showMessage(contains("Total: 2"));
        verify(view).showMessage(contains("Easy: 1"));
        verify(view).showMessage(contains("Recent certificates"));
        verify(view).showMessage(contains("Desc1"));
        verify(view).showMessage(contains("Desc2"));
    }

    @Test
    public void testResumeSavedGame_noSavedShowsMessage() throws Exception {
        when(progressService.getUserProgress("u1")).thenReturn(mockProgress);
        when(mockProgress.hasGameInProgress()).thenReturn(false);

        callPrivate(controller, "resumeSavedGame", new Class[]{});

        verify(view).showMessage(contains("No saved game found"));
        verify(mockProgress, never()).getCurrentPuzzleId();
    }

    @Test
    public void testResumeSavedGame_savedPuzzleNotFound_clearsAndSaves() throws Exception {
        when(progressService.getUserProgress("u1")).thenReturn(mockProgress);
        when(mockProgress.hasGameInProgress()).thenReturn(true);
        when(mockProgress.getCurrentPuzzleId()).thenReturn("P_NOT_EXIST");
        when(dataFacade.getPuzzle("P_NOT_EXIST")).thenReturn(Optional.empty());

        callPrivate(controller, "resumeSavedGame", new Class[]{});

        verify(view).showMessage(contains("Saved puzzle not found"));
        verify(mockProgress).clearGameState();
        verify(dataFacade).saveUserProgress(mockProgress);
    }

    @Test
    public void testSaveGame_persistsState_and_showsMessage() throws Exception {
        // prepare puzzle and game mocks
        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUserId()).thenReturn("u1");
        when(progressService.getUserProgress("u1")).thenReturn(mockProgress);

        Map<String,Object> saved = new HashMap<>();
        saved.put("k","v");
        when(mockGame.saveState()).thenReturn(saved);
        when(mockPuzzle.getPuzzleId()).thenReturn("PZ1");

        // call private saveGame
        callPrivate(controller, "saveGame", new Class[]{Puzzle.class, PuzzleGame.class}, mockPuzzle, mockGame);

        // verify interactions
        verify(mockProgress).saveGameState("PZ1", saved);
        verify(dataFacade).saveUserProgress(mockProgress);
        verify(view).showMessage(contains("Game saved"));
    }
}