package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory class to create a random set of puzzles
 * @author We're Getting an A
 */
public class RandomPuzzleFactory implements PuzzleFactory {

    /**
     * Creates a random set of puzzles from the list
     * @param allPuzzles List of all available puzzles
     * @return A list of puzzles that are not completed
     */

    @Override
    public List<Puzzle> createPuzzleSet(List<Puzzle> allPuzzles) {
        List<Puzzle> puzzleSet = new ArrayList<>();
        Random random = new Random();

        // If no puzzles provided, create small default list so method still works.
        if (allPuzzles == null || allPuzzles.isEmpty()) {
            allPuzzles = createSamplePuzzles();
        }

        // Choose how many puzzles to return (at least 1)
        int numberOfPuzzles = random.nextInt(allPuzzles.size()) + 1;

        // Keep selecting random puzzles until we have the correct amount
        while (puzzleSet.size() < numberOfPuzzles) {
            Puzzle puzzle = allPuzzles.get(random.nextInt(allPuzzles.size()));
            // use the existing checkComplete() method to avoid duplicates
            if (!puzzle.checkComplete() && !puzzleSet.contains(puzzle)) {
                puzzleSet.add(puzzle);
            }
            if (puzzleSet.size() >= allPuzzles.size()) {
                break;
            }
        }
        return puzzleSet;
    }

    /**
     * List of example puzzles
     * Used as a fallback when no puzzles are supplied
     */
    private List<Puzzle> createSamplePuzzles() {
    List<Puzzle> puzzles = new ArrayList<>();

        /**
         * Riddles
         */
        Puzzle riddleEasy = new Puzzle();
        riddleEasy.setPuzzleId("riddle-clock");
        riddleEasy.setDifficulty(DifficultyLevel.EASY);
        riddleEasy.setMaxAttempts(5);
        WordPuzzle.configureRiddle(riddleEasy,
            "I have a face and two hands but no arms or legs. What am I?",
            "clock");
        puzzles.add(riddleEasy);

        Puzzle riddleHard = new Puzzle();
        riddleHard.setPuzzleId("riddle-keyboard");
        riddleHard.setDifficulty(DifficultyLevel.HARD);
        riddleHard.setMaxAttempts(3);
        WordPuzzle.configureRiddle(riddleHard,
            "I have keys but no locks. I have space but no room. You can enter but not go outside. What am I?",
            "keyboard");
        puzzles.add(riddleHard);

        /**
         * Anagrams
         */
        Puzzle anagramEasy = new Puzzle();
        anagramEasy.setPuzzleId("anagram-apple");
        anagramEasy.setDifficulty(DifficultyLevel.EASY);
        anagramEasy.setMaxAttempts(5);
        WordPuzzle.configureAnagram(anagramEasy, "PEPLA", "apple");
        puzzles.add(anagramEasy);

        Puzzle anagramMedium = new Puzzle();
        anagramMedium.setPuzzleId("anagram-stream");
        anagramMedium.setDifficulty(DifficultyLevel.MEDIUM);
        anagramMedium.setMaxAttempts(5);
        WordPuzzle.configureAnagram(anagramMedium, "MASTER", "stream");
        puzzles.add(anagramMedium);

        /**
         * Ciphers
         */
        Puzzle cipherMedium = new Puzzle();
        cipherMedium.setPuzzleId("cipher-seeyou");
        cipherMedium.setDifficulty(DifficultyLevel.MEDIUM);
        cipherMedium.setMaxAttempts(3);
        WordPuzzle.configureCipher(cipherMedium, "Vhh brx vrrq", "see you soon", 3);
        puzzles.add(cipherMedium);

        Puzzle cipherHard = new Puzzle();
        cipherHard.setPuzzleId("cipher-quint");
        cipherHard.setDifficulty(DifficultyLevel.HARD);
        cipherHard.setMaxAttempts(3);
        WordPuzzle.configureCipher(cipherHard, "S T E N I Q L S E N T U I A", "quintessential", null);
        puzzles.add(cipherHard);

        return puzzles;
    }
}
