package com.escapegame.controller;

import com.model.Puzzle;

/**
 * SessionData - Simple session state holder
 * 
 * Stores transient session data that needs to be passed between views:
 * - Current difficulty
 * - Current puzzle being played
 * - Which door was clicked
 * 
 * NOTE: Puzzle COMPLETION is tracked by the backend (UserProgress.completedPuzzles)
 * This class only holds temporary navigation state.
 */
public class SessionData {
    
    private static String difficulty = "EASY";
    private static Puzzle currentPuzzle;
    private static int currentDoorNumber = -1;
    private static boolean isResuming = false;

    // ==================== DIFFICULTY ====================
    
    public static String getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(String diff) {
        difficulty = diff;
        System.out.println("SessionData: Difficulty set to " + diff);
    }

    // ==================== CURRENT PUZZLE ====================
    
    public static Puzzle getCurrentPuzzle() {
        return currentPuzzle;
    }

    public static void setCurrentPuzzle(Puzzle puzzle) {
        currentPuzzle = puzzle;
        if (puzzle != null) {
            System.out.println("SessionData: Current puzzle set to " + puzzle.getPuzzleId());
        }
    }

    // ==================== CURRENT DOOR ====================
    
    public static int getCurrentDoorNumber() {
        return currentDoorNumber;
    }

    public static void setCurrentDoorNumber(int doorNumber) {
        currentDoorNumber = doorNumber;
        System.out.println("SessionData: Current door set to " + doorNumber);
    }

    // ==================== RESUMING FLAG ====================
    
    public static boolean isResuming() {
        return isResuming;
    }

    public static void setResuming(boolean resuming) {
        isResuming = resuming;
    }

    // ==================== CLEAR/RESET ====================
    
    public static void clearSession() {
        currentPuzzle = null;
        currentDoorNumber = -1;
        isResuming = false;
        System.out.println("SessionData: Session cleared");
    }

    public static void reset() {
        difficulty = "EASY";
        clearSession();
        System.out.println("SessionData: Full reset");
    }
}