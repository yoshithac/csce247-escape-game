package com.model;
import java.util.List;

/**
 * Factory class for creating puzzle instances
 * @author We're Getting an A
 */
public interface PuzzleFactory {
    /**
     * Abstract method for creating a set of puzzles from the available puzzles
     * @param allPuzzles The list of all available puzzles
     * @return A list of puzzles that are not completed
     */
    public List<Puzzle> createPuzzleSet(List<Puzzle> allPuzzles);
}
