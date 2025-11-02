package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * JUnit4 tests for WordPuzzleGame (tests non-getter/setter behavior)
 */
public class WordPuzzleGameTest {

    private Map<String, Object> makePuzzleData(String prompt, String answer, String category, int maxAttempts) {
        Map<String, Object> data = new HashMap<>();
        data.put("prompt", prompt);
        data.put("answer", answer);
        data.put("category", category);
        data.put("maxAttempts", maxAttempts);
        return data;
    }

    private List<Hint> makeHints(String... texts) {
        List<Hint> list = new ArrayList<>();
        int pr = 1;
        for (String t : texts) {
            list.add(new Hint(t, "p1", pr++));
        }
        return list;
    }

    @Test
    public void testInitialize_andGetGameState_basics() {
        WordPuzzleGame game = new WordPuzzleGame();
        Map<String, Object> puzzle = makePuzzleData("Solve this", "answer", "GEN", 3);
        game.initialize(puzzle);

        Map<String, Object> state = game.getGameState();
        assertEquals("prompt should be set", "Solve this", state.get("prompt"));
        assertEquals("category should be set", "GEN", state.get("category"));
        assertEquals("attemptsUsed should start at 0", 0, ((Number) state.get("attemptsUsed")).intValue());
        assertEquals("maxAttempts should be correct", 3, ((Number) state.get("maxAttempts")).intValue());
        assertEquals("remainingAttempts should equal maxAttempts", 3, ((Number) state.get("remainingAttempts")).intValue());
        assertTrue("guesses should be an empty list", ((List) state.get("guesses")).isEmpty());
    }

    @Test
    public void testProcessInput_correctAnswer_setsWonAndProcessedTrue() {
        WordPuzzleGame game = new WordPuzzleGame();
        Map<String, Object> puzzle = makePuzzleData("Q", "SeCrEt", "CAT", 3);
        game.initialize(puzzle);

        // answer is case-insensitive (game uppercases internally)
        boolean processed = game.processInput("secret");
        assertTrue("processInput should return true for correct answer", processed);
        Map<String, Object> result = game.getResult();
        assertTrue("won should be true after correct answer", (Boolean) result.get("won"));
        assertEquals("moves should be 0 after immediate correct answer", 0, ((Number) result.get("moves")).intValue());
    }

    @Test
    public void testProcessInput_wrongGuesses_incrementAttempts_andPreventDuplicateGuessAddition() {
        WordPuzzleGame game = new WordPuzzleGame();
        Map<String, Object> puzzle = makePuzzleData("Q", "OK", "CAT", 5);
        game.initialize(puzzle);

        // first wrong guess
        assertTrue("first wrong guess processed", game.processInput("NO"));
        Map<String, Object> s1 = game.getGameState();
        assertEquals("attemptsUsed should be 1", 1, ((Number) s1.get("attemptsUsed")).intValue());
        assertEquals("guesses should contain one entry", 1, ((List) s1.get("guesses")).size());

        // second wrong guess, different
        assertTrue("second wrong guess processed", game.processInput("MAYBE"));
        Map<String, Object> s2 = game.getGameState();
        assertEquals("attemptsUsed should be 2", 2, ((Number) s2.get("attemptsUsed")).intValue());
        assertEquals("guesses should contain two entries", 2, ((List) s2.get("guesses")).size());

        // repeat the same guess "MAYBE" - attemptsUsed should still increment but guesses list should not add duplicate
        assertTrue("duplicate guess processed", game.processInput("MAYBE"));
        Map<String, Object> s3 = game.getGameState();
        assertEquals("attemptsUsed should be 3 after duplicate guess", 3, ((Number) s3.get("attemptsUsed")).intValue());
        assertEquals("guesses should still have two unique entries", 2, ((List) s3.get("guesses")).size());
    }

    @Test
    public void testHintRequest_revealsHints_andAvailableHintsCountUpdates() {
        WordPuzzleGame game = new WordPuzzleGame();
        Map<String, Object> puzzle = makePuzzleData("Q", "A", "CAT", 3);
        game.initialize(puzzle);

        List<Hint> hints = makeHints("first", "second", "third");
        game.setHints(hints);

        // initially no revealed hints
        Map<String, Object> s0 = game.getGameState();
        assertEquals("availableHintsCount should equal total hints initially", 3, ((Number) s0.get("availableHintsCount")).intValue());

        // request a hint
        assertTrue("HINT input processed", game.processInput("HINT"));
        Map<String, Object> s1 = game.getGameState();
        assertEquals("availableHintsCount should decrease after one reveal", 2, ((Number) s1.get("availableHintsCount")).intValue());
        List revealed1 = (List) s1.get("revealedHints");
        assertEquals("one hint should be revealed", 1, revealed1.size());
        assertEquals("revealed hint text should match first hint", "first", revealed1.get(0));

        // request another hint (case-insensitive)
        assertTrue(game.processInput("hint"));
        Map<String, Object> s2 = game.getGameState();
        assertEquals(1, ((Number) s2.get("attemptsUsed")).intValue()); // hint does not increment attempts
        assertEquals("availableHintsCount should be 1", 1, ((Number) s2.get("availableHintsCount")).intValue());
        List revealed2 = (List) s2.get("revealedHints");
        assertEquals(2, revealed2.size());
    }

    @Test
    public void testIsGameOver_whenAttemptsExhausted_andGetResultShowsAnswerWhenLost() {
        WordPuzzleGame game = new WordPuzzleGame();
        Map<String, Object> puzzle = makePuzzleData("Q", "WIN", "X", 2);
        game.initialize(puzzle);

        // two wrong attempts will exhaust maxAttempts
        game.processInput("NO1");
        game.processInput("NO2");
        assertTrue("game should be over after exhausting attempts", game.isGameOver());

        Map<String, Object> result = game.getResult();
        assertFalse("won should be false when lost", (Boolean) result.get("won"));
        assertEquals("moves should equal attempts used", 2, ((Number) result.get("moves")).intValue());
        assertEquals("answer should be included in result when not won", "WIN", result.get("answer"));
    }

    @Test
    public void testReset_clearsGuesses_andRevealedHints_andAttempts() {
        WordPuzzleGame game = new WordPuzzleGame();
        Map<String, Object> puzzle = makePuzzleData("Q", "ANS", "C", 3);
        game.initialize(puzzle);
        game.setHints(makeHints("h1","h2"));

        // use up some state
        game.processInput("NO");
        game.processInput("HINT");
        assertTrue("some state should have changed", game.getGameState().get("revealedHints") != null);

        game.reset();
        Map<String, Object> s = game.getGameState();
        assertEquals("attemptsUsed reset to 0", 0, ((Number) s.get("attemptsUsed")).intValue());
        assertEquals("revealedHints cleared after reset", 0, ((List) s.get("revealedHints")).size());
        assertEquals("guesses cleared after reset", 0, ((List) s.get("guesses")).size());
        assertFalse("won should be false after reset", (Boolean) game.getResult().get("won"));
    }

    @Test
    public void testSaveState_andRestoreState_roundTrip_preservesRevealedHintsAndGuesses() {
        WordPuzzleGame game = new WordPuzzleGame();
        Map<String, Object> puzzle = makePuzzleData("Q", "SECRET", "cat", 5);
        game.initialize(puzzle);
        game.setHints(makeHints("h1","h2","h3"));

        // add guesses and reveal two hints
        game.processInput("TRY1");
        game.processInput("HINT");
        game.processInput("TRY2");
        game.processInput("HINT");

        Map<String, Object> saved = game.saveState();

        // Create fresh game and restore
        WordPuzzleGame restored = new WordPuzzleGame();
        restored.restoreState(saved);
        // set hints on restored so hint-related behavior is consistent (restoreState sets hints to empty list)
        restored.setHints(makeHints("h1","h2","h3"));

        Map<String, Object> rstate = restored.getGameState();
        assertEquals("attemptsUsed should be restored", ((Number) saved.get("attemptsUsed")).intValue(), ((Number) rstate.get("attemptsUsed")).intValue());
        assertEquals("guesses should be restored", ((List) saved.get("guesses")).size(), ((List) rstate.get("guesses")).size());
        assertEquals("revealedHints should be restored", ((List) saved.get("revealedHints")).size(), ((List) rstate.get("revealedHints")).size());
    }
}