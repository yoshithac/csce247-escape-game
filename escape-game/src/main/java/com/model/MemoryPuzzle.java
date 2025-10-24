package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*
 * MemoryPuzzle extends Puzzle and implements two memory games.
 * CardMatch: 2x3 grid with 2 GREEN, 2 RED, 2 BLUE cards
 * AudioMatch: random sequence of 3 instruments to guess
 *
 * Author: We're Getting an A
 */
public class MemoryPuzzle extends Puzzle {
    private static final int CARD_ROWS = 2;
    private static final int CARD_COLS = 3;

    
    private Card[][] grid;
    private int cardAttempts;
    private int cardMatchesFound;

   
    private final List<String> instruments = new ArrayList<>();
    private List<String> audioSequence;
    private int audioAttempts;
    private int audioHintsUsed;

    
    private final Random random;

    
    private static class Card {
        String color;
        boolean matched;

        Card(String color) {
            this.color = color;
            this.matched = false;
        }
    }

    /**
     * Full constructor matching Puzzle signature
     */
    public MemoryPuzzle(String puzzleId,
                        String type,
                        String prompt,
                        String answer,
                        String description,
                        DifficultyLevel difficulty,
                        int maxAttempts) {
        super(puzzleId, type, prompt, answer, description, difficulty, maxAttempts);
        this.random = new Random(System.currentTimeMillis());
        CardGame();
        AudioGame();
    }

    /**
     * Convenience constructor
     */
    public MemoryPuzzle() {
        super("memory-001",
              "Memory",
              "A random memory mini-game will be chosen and you must solve it.",
              "solve",
              "Two mini-games: color card match or audio sequence guess.",
              DifficultyLevel.MEDIUM,
              10);
        this.random = new Random(System.currentTimeMillis());
        CardGame();
        AudioGame();
    }

    
    private void CardGame() {
        cardAttempts = 0;
        cardMatchesFound = 0;

        List<String> colors = new ArrayList<>();
        colors.add("GREEN"); colors.add("GREEN");
        colors.add("RED");   colors.add("RED");
        colors.add("BLUE");  colors.add("BLUE");

        Collections.shuffle(colors, random);

        grid = new Card[CARD_ROWS][CARD_COLS];
        int idx = 0;
        for (int r = 0; r < CARD_ROWS; r++) {
            for (int c = 0; c < CARD_COLS; c++) {
                grid[r][c] = new Card(colors.get(idx++));
            }
        }
    }

    private boolean validCardCoord(int r, int c) {
        return r >= 0 && r < CARD_ROWS && c >= 0 && c < CARD_COLS;
    }

   
    public void CardMatch() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Card Color Match (2 x 3 grid) ---");
        System.out.println("Find 3 pairs (2 GREEN, 2 RED, 2 BLUE). Coordinates: row,col (0-based), e.g. 0,1");

        
        CardGame();

        while (!isCardMatchCompleted()) {
            printMaskedGrid();
            System.out.print("Enter first card (row,col) or 'q' to quit: ");
            String first = scanner.nextLine().trim();
            if (first.equalsIgnoreCase("q")) break;
            System.out.print("Enter second card (row,col) or 'q' to quit: ");
            String second = scanner.nextLine().trim();
            if (second.equalsIgnoreCase("q")) break;

            try {
                String[] a = first.split(",");
                String[] b = second.split(",");
                int r1 = Integer.parseInt(a[0].trim());
                int c1 = Integer.parseInt(a[1].trim());
                int r2 = Integer.parseInt(b[0].trim());
                int c2 = Integer.parseInt(b[1].trim());

                if (!validCardCoord(r1, c1) || !validCardCoord(r2, c2)) {
                    System.out.println("Invalid coordinates. Try again.");
                    continue;
                }
                if (r1 == r2 && c1 == c2) {
                    System.out.println("You selected the same card twice. Try again.");
                    continue;
                }

                String color1 = revealCardColor(r1, c1);
                String color2 = revealCardColor(r2, c2);
                System.out.println("You revealed: " + color1 + " and " + color2);

                boolean matched = checkCardMatch(r1, c1, r2, c2);
                if (matched) System.out.println("It's a match!");
                else System.out.println("Not a match.");

                System.out.println("Attempts: " + cardAttempts);
            } catch (Exception ex) {
                System.out.println("Invalid input. Use row,col (e.g. 0,1).");
            }
        }

        if (isCardMatchCompleted()) {
            System.out.println("Well done — you found all pairs in " + cardAttempts + " attempts.");
            setCompleted(true);
        } else {
            System.out.println("Card game ended. Pairs found: " + cardMatchesFound);
        }
    }

    private void printMaskedGrid() {
        System.out.println("Current grid (matched cards shown; others '*'):");
        for (int r = 0; r < CARD_ROWS; r++) {
            for (int c = 0; c < CARD_COLS; c++) {
                System.out.print((grid[r][c].matched ? grid[r][c].color : "*") + "\t");
            }
            System.out.println();
        }
    }

    private boolean checkCardMatch(int row1, int col1, int row2, int col2) {
        if (!validCardCoord(row1, col1) || !validCardCoord(row2, col2)) return false;
        if (row1 == row2 && col1 == col2) return false;

        Card a = grid[row1][col1];
        Card b = grid[row2][col2];

        if (a.matched || b.matched) return false;

        cardAttempts++;

        if (a.color.equals(b.color)) {
            a.matched = true;
            b.matched = true;
            cardMatchesFound++;
            return true;
        } else {
            return false;
        }
    }

    private String revealCardColor(int row, int col) {
        if (!validCardCoord(row, col)) return null;
        return grid[row][col].color;
    }

    private boolean isCardMatchCompleted() {
        return cardMatchesFound >= 3;
    }

   
    private void AudioGame() {
        instruments.clear();
        instruments.add("Piano");
        instruments.add("Guitar");
        instruments.add("Drums");

        audioSequence = new ArrayList<>(instruments);
        Collections.shuffle(audioSequence, random);
        audioAttempts = 0;
        audioHintsUsed = 0;
    }

    
    public void AudioMatch() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Audio Sequence Match ---");
        System.out.println("Guess the order of: Piano, Guitar, Drums");
        System.out.println("Enter guess with comma separated names (e.g. Piano,Guitar,Drums). Type 'hint' or 'q'.");

       
        AudioGame();

<<<<<<< HEAD

=======
        while (true) {
            System.out.print("Enter guess, 'hint', or 'q': ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("q")) break;
            if (line.equalsIgnoreCase("hint")) {
                List<String> hint = useAudioHint();
                System.out.println("Hint: " + String.join(", ", hint));
                continue;
            }

            String[] parts = line.split(",");
            List<String> guess = new ArrayList<>();
            for (String p : parts) guess.add(p.trim());

            boolean ok = checkAudioGuess(guess);
            if (ok) {
                System.out.println("Correct! Solved in " + audioAttempts + " attempts.");
                setCompleted(true);
                break;
            } else {
                System.out.println("Incorrect. Attempts so far: " + audioAttempts);
            }
        }
    }

    private boolean checkAudioGuess(List<String> guess) {
        audioAttempts++;
        if (guess == null || guess.size() != audioSequence.size()) return false;
        for (int i = 0; i < audioSequence.size(); i++) {
            String expected = audioSequence.get(i);
            String given = guess.get(i);
            if (given == null || !expected.equalsIgnoreCase(given.trim())) return false;
        }
        return true;
    }

    private List<String> useAudioHint() {
        audioHintsUsed = Math.min(audioHintsUsed + 1, audioSequence.size());
        return new ArrayList<>(audioSequence.subList(0, audioHintsUsed));
    }
>>>>>>> 635f783739aea10daf99529ecba9d40bb414247b

    /**
     * Now auto-selects a mini-game at random (user does not choose).
     */
    @Override
    public void playPuzzle() {
        
        boolean pickCard = random.nextBoolean();
        if (pickCard) {
            System.out.println("A mini-game was chosen at random: CARD MATCH");
            CardMatch();
        } else {
            System.out.println("A mini-game was chosen at random: AUDIO SEQUENCE");
            AudioMatch();
        }
    }
}
