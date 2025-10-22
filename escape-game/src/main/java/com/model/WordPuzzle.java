package com.model;

/**
 * Represents a word puzzle in the escape game
 * @author We're Getting an A
 */
public class WordPuzzle {

    /**
     * Configure a puzzle as a RIDDLE.
     * @param p the puzzle to configure
     * @param riddle the riddle text shown to the player
     * @param answer the correct answer (plain text)
     */
    public void Riddle(Puzzle p, String riddle, String answer) {
        if (p == null) return;
        p.setType("RIDDLE");
        p.setPrompt(riddle);
        p.setAnswer(answer);
        p.setDescription("Riddle");
    }

    /**
     * Configure a Puzzle as an ANAGRAM.
     * @param p the Puzzle to configure
     * @param scrambled the scrambled letters shown to the player
     * @param solution the correct unscrambled answer
     */
    public void Anagram(Puzzle p, String scrambled, String solution) {
        if (p == null) return;
        p.setType("ANAGRAM");
        p.setPrompt("Unscramble: " + scrambled);
        p.setAnswer(solution);
        p.setDescription("Anagram");
    }

    /**
     * Configure a puzzle as a CIPHER.
     * @param p the puzzle to configure
     * @param ciphertext the ciphertext shown to players
     * @param plaintext the decoded answer
     * @param key optional Caesar shift key (null if not applicable)
     */
    public void Cipher(Puzzle p, String ciphertext, String plaintext, Integer key) {
        if (p == null) return;
        p.setType("CIPHER");
        p.setPrompt(ciphertext);
        p.setAnswer(plaintext);
        if (key == null) {
            p.setDescription("Cipher");
        } else {
            p.setDescription("Cipher. KEY:" + key);
        }
    }
}
