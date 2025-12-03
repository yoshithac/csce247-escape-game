package com.model;

import java.util.Map;

public class GameFactory {

    public static PuzzleGame createGame(String puzzleType) {
        return createGame(puzzleType, "MEDIUM");
    }

    public static PuzzleGame createGame(String puzzleType, String difficulty) {
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
                wordGame.setPuzzleId(puzzleType.toLowerCase() + "_" + difficulty.toLowerCase());
                Map<String, Object> cfg = WordPuzzleGame.configFor(puzzleType, difficulty);
                if (cfg != null) {
                    wordGame.initialize(cfg);
                }
                return wordGame;

            default:
                throw new IllegalArgumentException("Unknown puzzle type: " + puzzleType);
        }
    }
}
