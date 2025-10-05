package com.model;

import java.util.List;

public class GameDataFacade {
    private static GameDataFacade gameDataFacade;
    private GameDataLoader gameDataLoader;
    private GameDataWriter gameDataWriter;
    private List<Puzzle> puzzles;
    private List<User> users;
    private GameData gameData;
    private List<Certificate> certificate;
    private Leaderboard leaderboard;

    private GameDataFacade() {
        gameDataLoader = new GameDataLoader();
        gameDataWriter = new GameDataWriter();
        gameData = new GameData();
    }

    public static GameDataFacade getInstance() {
        if (gameDataFacade == null) {
            gameDataFacade = new GameDataFacade();
        }
        return gameDataFacade;
    }

    public User getUser(String userId) {
        if (users == null) return null;
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public List<Certificate> getCertificates(String userId) {
        if (certificate == null) return null;
        return certificate.stream()
                .filter(cert -> cert.getCertUserId().equals(userId))
                .toList();
    }

    public List<Hint> getHints() {
        return gameData != null ? gameData.getHints() : null;
    }

    public GameData loadGameData() {
        if (gameDataLoader != null) {
            gameData = gameDataLoader.readGameData();
        }
        return gameData;
    }

    public List<User> loadUsers() {
        if (gameDataLoader != null) {
            users = gameDataLoader.readUsers();
        }
        return users;
    }

    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

    public void saveUser(User user) {
        if (gameDataWriter != null) {
            gameDataWriter.writeUser(user);
        }
    }

    public void saveGameData(GameData gameData) {
        if (gameDataWriter != null) {
            gameDataWriter.writeGameData(gameData);
        }
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setPuzzles(List<Puzzle> puzzles) {
        this.puzzles = puzzles;
    }

    public void setCertificate(List<Certificate> certificate) {
        this.certificate = certificate;
    }

    @Override
    public String toString() {
        return "GameDataFacade{" +
                "puzzles=" + puzzles +
                ", users=" + users +
                ", gameData=" + gameData +
                ", certificate=" + certificate +
                ", leaderboard=" + leaderboard +
                '}';
    }
}
