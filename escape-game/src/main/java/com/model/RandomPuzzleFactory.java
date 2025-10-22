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
     * Creates a random set of puzzles from the available puzzles
     * @param allPuzzles The list of all available puzzles
     * @return A list of puzzles that are not completed
     */
    public List<Puzzle> createPuzzleSet(List<Puzzle> allPuzzles) {
        List<Puzzle> puzzleSet = new ArrayList<>();
        Random random = new Random();
        int numberOfPuzzles = random.nextInt(allPuzzles.size()) + 1;

        for (int i = 0; i < numberOfPuzzles; i++) {
            Puzzle puzzle = allPuzzles.get(random.nextInt(allPuzzles.size()));
            if (!puzzle.checkComplete()) {
                puzzleSet.add(puzzle);
            }
        }
        return puzzleSet;
    }
}
