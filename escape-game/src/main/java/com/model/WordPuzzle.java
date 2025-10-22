package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contains six word puzzles, two of each: riddles, anagrams, and ciphers.
 * @author We're Getting an A
 */
public class WordPuzzle {

    private static class Puzzle {
        String type;
        String question;
        String answer;
        String clue;
        boolean solved = false;
        int attempts = 0;

        Puzzle(String type, String question, String answer, String clue) {
            this.type = type;
            this.question = question;
            this.answer = answer.toLowerCase();
            this.clue = clue;
        }

        boolean checkAnswer(String attempt) {
            attempts++;
            if (attempt == null) return false;
            return attempt.trim().equalsIgnoreCase(answer);
        }
    }

    private final List<Puzzle> puzzles = new ArrayList<>();
    private long startTime;
    private int score;

    public WordPuzzle() {
        initPuzzles();
    }

    private void initPuzzles() {
        puzzles.add(new Puzzle("Riddle",
                "I have a face and two hands but no arms or legs. What am I?",
                "clock",
                "It keeps time."));
        puzzles.add(new Puzzle("Anagram",
                "Rearrange the letters of PEPLA to make a single common English word (clue: fruit).",
                "apple",
                "It’s something you can eat."));
        puzzles.add(new Puzzle("Anagram",
                "Rearrange the letters of MASTER to make a single common English word (clue: fish).",
                "stream",
                "Think of water."));
        puzzles.add(new Puzzle("Cipher",
                "Decode this: Vhh brx vrrq (KEY: 3 to the right)",
                "see you soon",
                "Caesar cipher, shift by 3."));
        puzzles.add(new Puzzle("Riddle",
                "I have keys but no locks. I have space but no room. You can enter but not go outside. What am I?",
                "keyboard",
                "You’re using one right now."));
        puzzles.add(new Puzzle("Cipher",
                "Decode this: S T E N I Q L S E N T U I A",
                "quintessential",
                "Think of a long word meaning ‘the perfect example’."));
    }

    private void showIntro() {
        System.out.println("=================================");
        System.out.println("       WORD PUZZLE CHALLENGE     ");
        System.out.println("=================================");
        System.out.println("Solve the puzzles as quickly and accurately as you can!");
        System.out.println("Score decreases with time and number of attempts.\n");
    }

    private void showScore(long elapsedSeconds, int totalAttempts) {
        score = Math.max(0, 300 - (int)(elapsedSeconds * 10 + totalAttempts * 5));
        System.out.println("\n=================================");
        System.out.println("      ALL PUZZLES COMPLETED!");
        System.out.println("Time: " + elapsedSeconds + " seconds");
        System.out.println("Total Attempts: " + totalAttempts);
        System.out.println("Score: " + score);
        System.out.println("=================================");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        WordPuzzle game = new WordPuzzle();
        game.showIntro();

        game.startTime = System.currentTimeMillis();
        int totalAttempts = 0;

        for (int i = 0; i < game.puzzles.size(); i++) {
            Puzzle p = game.puzzles.get(i);
            System.out.println("\nPuzzle " + (i + 1) + " (" + p.type + "):");
            System.out.println(p.question);
            if (p.clue != null && !p.clue.isEmpty()) {
                System.out.println("Clue: " + p.clue);
            }

            while (!p.solved) {
                System.out.print("Your answer: ");
                String guess = sc.nextLine();
                if (p.checkAnswer(guess)) {
                    p.solved = true;
                    System.out.println("Correct!");
                } else {
                    System.out.println("Try again.");
                }
            }
            totalAttempts += p.attempts;
        }

        long elapsedSeconds = (System.currentTimeMillis() - game.startTime) / 1000;
        game.showScore(elapsedSeconds, totalAttempts);

        sc.close();
    }
}
