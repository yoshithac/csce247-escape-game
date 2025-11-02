package com.model;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test cases for GameFactory
 * Each test method contains exactly one assertion
 */
public class GameFactoryTest {
    
    @Test
    public void testCreateGameReturnsMazeGameForMazeType() {
        PuzzleGame game = GameFactory.createGame("MAZE");
        assertTrue(game instanceof MazeGame);
    }
    
    @Test
    public void testCreateGameReturnsMatchingGameForMatchingType() {
        PuzzleGame game = GameFactory.createGame("MATCHING");
        assertTrue(game instanceof MatchingGame);
    }
    
    @Test
    public void testCreateGameReturnsWordPuzzleGameForCipherType() {
        PuzzleGame game = GameFactory.createGame("CIPHER");
        assertTrue(game instanceof WordPuzzleGame);
    }
    
    @Test
    public void testCreateGameReturnsWordPuzzleGameForAnagramType() {
        PuzzleGame game = GameFactory.createGame("ANAGRAM");
        assertTrue(game instanceof WordPuzzleGame);
    }
    
    @Test
    public void testCreateGameReturnsWordPuzzleGameForRiddleType() {
        PuzzleGame game = GameFactory.createGame("RIDDLE");
        assertTrue(game instanceof WordPuzzleGame);
    }
    
    @Test
    public void testCreateGameIsCaseInsensitive() {
        PuzzleGame game = GameFactory.createGame("maze");
        assertTrue(game instanceof MazeGame);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGameThrowsExceptionForNullType() {
        GameFactory.createGame(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGameThrowsExceptionForUnknownType() {
        GameFactory.createGame("UNKNOWN");
    }
}
