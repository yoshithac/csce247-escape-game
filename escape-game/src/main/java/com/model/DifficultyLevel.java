package com.model;

public enum DifficultyLevel {
    EASY,
    MEDIUM,
    HARD;

    public int getMaxMistakes() {
        switch (this) {
            case EASY: return 5;
            case MEDIUM: return 3;
            case HARD: return 1;
            default: return 0;
        }
    }

    public int getMaxHints(){
        switch (this) {
            case EASY: return 5;
            case MEDIUM: return 3;
            case HARD: return 1;
            default: return 0;
        }
    }
}
