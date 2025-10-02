package com.model;

import java.util.List;
import java.util.Timer;

public class GameManager {
    private User currentPlayer;
    private List<User> players;
    private DifficultyLevel difficulty;
    private int startTime;
    private boolean isActive;
    private GameProgress currentProgress;
    private List<Puzzle> currentPuzzles;
    private List<Hint> hints;
    private Timer sessionTimer;
    
    public GameManager() {

    }
    public void startGame() {

    }
    public void loadGame(String userId) {

    }
    public void saveGame(String userId) {

    }
    public void pauseGame() {

    }
    public void showLeaderboard() {

    }
    public void endGame() {

    }
    public void registerUser(Object userData) {

    }
    public void startNextPuzzle() {

    }
    public boolean loginUser(String userId, String password) {
        return true;
    }
    public void deleteUser(String userId) {

    }
    public void recoverAccount (String userId) {

    }
    public void loadUsers() {

    }
    public void loadGameData() {

    }
    public boolean checkGameOver() {
        return true;
    }
    public void handleGameEnd() {

    }
    public void startTimer() {

    }
    public int getRemainingTime() {
        return 0;
    }
    public boolean isTimeUp() {
        return true;
    }
}

