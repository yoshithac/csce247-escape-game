package com.model;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test cases for GameFactory
 * Each test method contains exactly one assertion
 *
 * Verifies that {@link GameFactory} correctly instantiates
 * the appropriate {@link PuzzleGame} subclass based on the
 * provided puzzle type string, and handles invalid inputs properly.
 */
public class GameFactoryTest {
    
    /**
     * Ensures that the factory returns a {@link MazeGame}
     * instance when the puzzle type is "MAZE".
     */
    @Test
    public void testCreateGameReturnsMazeGameForMazeType() {
        PuzzleGame game = GameFactory.createGame("MAZE");
        assertTrue(game instanceof MazeGame);
    }
    
    /**
     * Ensures that the factory returns a {@link MatchingGame}
     * instance when the puzzle type is "MATCHING".
     */
    @Test
    public void testCreateGameReturnsMatchingGameForMatchingType() {
        PuzzleGame game = GameFactory.createGame("MATCHING");
        assertTrue(game instanceof MatchingGame);
    }
    
    /**
     * Ensures that the factory returns a {@link WordPuzzleGame}
     * for the puzzle type "CIPHER".
     */
    @Test
    public void testCreateGameReturnsWordPuzzleGameForCipherType() {
        PuzzleGame game = GameFactory.createGame("CIPHER");
        assertTrue(game instanceof WordPuzzleGame);
    }
    
    /**
     * Ensures that the factory returns a {@link WordPuzzleGame}
     * for the puzzle type "ANAGRAM".
     */
    @Test
    public void testCreateGameReturnsWordPuzzleGameForAnagramType() {
        PuzzleGame game = GameFactory.createGame("ANAGRAM");
        assertTrue(game instanceof WordPuzzleGame);
    }
    
    /**
     * Ensures that the factory returns a {@link WordPuzzleGame}
     * for the puzzle type "RIDDLE".
     */
    @Test
    public void testCreateGameReturnsWordPuzzleGameForRiddleType() {
        PuzzleGame game = GameFactory.createGame("RIDDLE");
        assertTrue(game instanceof WordPuzzleGame);
    }
    
    /**
     * Confirms that puzzle type matching is case-insensitive.
     */
    @Test
    public void testCreateGameIsCaseInsensitive() {
        PuzzleGame game = GameFactory.createGame("maze");
        assertTrue(game instanceof MazeGame);
    }
    
    /**
     * Expects an IllegalArgumentException when puzzle type is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGameThrowsExceptionForNullType() {
        GameFactory.createGame(null);
    }
    
    /**
     * Expects an IllegalArgumentException when puzzle type is unrecognized.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGameThrowsExceptionForUnknownType() {
        GameFactory.createGame("UNKNOWN");
    }
}
