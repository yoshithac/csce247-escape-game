package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.Scanner;


/* 
* Represents a memory puzzle in the escape game 
* @author We're Getting an A
*/
public class MemoryPuzzle {
    private List<String> sequence;

    private static final int CARD_ROWS = 2;
    private static final int CARD_COLS = 3;
    private Card[][] grid;
    private int cardAttempts;
    private int cardMatchesFound;
    private final Random random;

    private final List<String> instruments = new ArrayList<>();
    private List<String> audioSequence;
    private int audioAttempts;
    private int audioHintsUsed;

    private static class Card{
        String color;
        boolean matched;

        Card(String color) {
            this.color = color;
            this.matched = false;
        }

    }


    /**
     * Constructor for MemoryPuzzle
     */
    public MemoryPuzzle() {
        sequence = new ArrayList<>();
        random = new Random(System.currentTimeMillis());
        CardGame();
        AudioGame();
    }

    private void CardGame() {
        cardAttempts = 0;
        cardMatchesFound = 0;
        List<String> colors = new ArrayList<>();
        colors.add("GREEN");
        colors.add("GREEN");
        colors.add("RED");
        colors.add("RED");
        colors.add("BLUE");
        colors.add("BLUE");

        Collections.shuffle(colors, random);
        grid = new Card[CARD_ROWS] [CARD_COLS];
        int idx = 0;
        for (int r = 0; r < CARD_ROWS; r++) {
            for (int c = 0; c < CARD_COLS; c++) {
                grid[r][c] = new Card(colors.get(idx++));
            }
        }

    }

    private boolean validCardCoord(int r, int c) {
        return r>= 0 && r < CARD_ROWS && c >= 0 && c < CARD_COLS;
    }

    /**
     * Simulates a card matching game
     */
    public void CardMatch() {
       Scanner scanner = new Scanner(System.in);
       System.out.println("\n--- Card Color Match (2 x 3 grid) ----");
       System.out.println("Find 3 pairs ( Must be 2 GREEN, 2 RED and 2 BLUE) Coordinates are 0-based; row,col (e.g. 0,1).");

       CardGame();

       while (!isCardMatchCompleted()) {
        printMaskedGrid();
        System.out.print("Enter first card by row,col or 'q' to quit: ");
        String first = scanner.nextLine().trim();
        if (first.equalsIgnoreCase("q")) break;
        System.out.print("Enter second card by row,col or 'q' to quit: ");
        String second = scanner.nextLine().trim();
        if (second.equalsIgnoreCase("q")) break;

        try {
            String[] a = first.split(",");
            String[] b = second.split(",");
            int r1 =Integer.parseInt(a[0].trim());
            int c1 =Integer.parseInt(a[1].trim());
            int r2 =Integer.parseInt(b[0].trim());
            int c2 =Integer.parseInt(b[1].trim());

            if (!validCardCoord(r1, c1) || !validCardCoord(r2, c2)){
                System.out.println("Invalid coordinates. Please try again.");
                continue;
            }
            if (r1 == r2 && c1 == c2) {
                System.out.println("You selected the same card twice. Please try again.");
                continue;   
            }
            
            String color1 = revealCardColor(r1, c1);
            String color2 = revealCardColor(r2, c2);
            System.out.println("You revealed: " + color1 + " and " + color2);

            boolean matched = checkCardMatch(r1, c1, r2, c2);
            if (matched) {
                System.out.println("It's a match.");
            } else {
                System.out.println("Not a match.");
            }
            System.out.println("Attempts: " + cardAttempts);
        } catch (Exception ex) {
            System.out.println("Invalid input format. Use row,col (e.g. 0,1).");
        }
    }
    if (isCardMatchCompleted()) {
        System.out.println("Congratulations, you found all the pairs in " + cardAttempts + " attempts");
    }
  }
  private void printMaskedGrid() {
    System.out.println("Current grid (matched cards are shown, others are '*'):");
    for (int r = 0; r < CARD_ROWS; r++) {
        for (int c = 0; c < CARD_COLS; c++) {
            System.out.print((grid[r][c].matched ? grid[r][c].color : "*") + "\t");
        }
        System.out.println();
    }
}
    private String revealCardColor(int r, int c) {
        if (!validCardCoord(r, c)) {
            throw new IllegalArgumentException("Invalid card coordinates: " + r + "," + c);  
        }
        return grid[r][c].color;
            
    }
    
    private boolean checkCardMatch(int r1, int c1, int r2, int c2) {
        cardAttempts++;

        Card card1 = grid[r1][c1];
        Card card2 = grid[r2][c2];

        if (card1.matched || card2.matched) {
            return true;
        } else {
            return false;
        }
    }
   

        
    




    /**
     * Simulates an audio matching game
     */
    public void AudioMatch() {
  
    }
}