package com.model;

/**
 * Factory class for creating PuzzleGame instances
 * Creates appropriate game type based on puzzleType string
 */
public class GameFactory {
    
    /**
     * Create a game instance based on puzzle type
     * @param puzzleType Type of puzzle (MAZE, MATCHING, CIPHER, ANAGRAM, RIDDLE)
     * @return PuzzleGame instance
     * @throws IllegalArgumentException if puzzle type is unknown
     */
    public static PuzzleGame createGame(String puzzleType) {
        if (puzzleType == null) {
            throw new IllegalArgumentException("Puzzle type cannot be null");
        }
        
        switch (puzzleType.toUpperCase()) {
            case "MAZE":
                return new MazeGame();
                
            case "MATCHING":
                return new MatchingGame();
                
            case "CIPHER":
            case "ANAGRAM":
            case "RIDDLE":
                WordPuzzleGame wordGame = new WordPuzzleGame();
                wordGame.setPuzzleType(puzzleType.toUpperCase());
                return wordGame;
                
            default:
                throw new IllegalArgumentException("Unknown puzzle type: " + puzzleType);
        }
    }
}