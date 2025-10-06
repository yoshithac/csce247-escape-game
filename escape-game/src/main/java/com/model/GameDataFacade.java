package com.model;
import java.util.List;
import java.util.ArrayList;


/*
 * This class acts as a facade for accessing and managing game data
 * @author We're Getting an A
 */
public class GameDataFacade {
    private static GameDataFacade gameDataFacade;
    private GameDataLoader gameDataLoader;
    private GameDataWriter gameDataWriter;
    private List<Puzzle> puzzles;
    private List<User> users;
    private GameData gameData;
    private List<Certificate> certificates;
    private Leaderboard leaderboard;
    private List<Hint> hints;

    private GameDataFacade() throws Exception {
        gameDataLoader = new GameDataLoader();
        users = gameDataLoader.loadUsers();
    }
    /*
     * Singleton instance of GameDataFacade
     * @return the single instance of GameDataFacade
    */
    public static GameDataFacade getInstance() throws Exception {
        if (gameDataFacade == null) {
            gameDataFacade = new GameDataFacade();
        }
        return gameDataFacade;
    }

    /**
     * Gets a user by their ID
     * @param userId The ID of the user to retrieve
     * @return The user with the specified ID, or null if not found
     */
    public User getUser(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Gets certificates for a specific user
     * @param userId The ID of the user whose certificates are to be retrieved
     * @return A list of certificates belonging to the specified user
     */
    public List<Certificate> getCertificates(String userId) {
        List<Certificate> userCertificates = new ArrayList<>();
        for (Certificate cert : certificates) {
            if (cert.getCertUserId().equals(userId)) {
                userCertificates.add(cert);
            }
        }
        return userCertificates;
    }

    /**
     * Gets hints for the current game session
     * @return A list of hints for the current game session
     */
    public List<Hint> getHints() {
        return hints;
    }

    /**
     * Loads game data from storage
     * @return The loaded game data
     */
    public GameData loadGameData() {
        return null;
    }

    /**
     * Loads users from storage
     * @return A list of loaded users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Gets the list of puzzles in the game
     * @return A list of puzzles
     */
    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

    /**
     * Saves a user to storage
     * @param user The user to be saved
     */
    public void saveUser(User user) {
        
    }

    /**
     * Saves game data to storage
     * @param gameData The game data to be saved
     */
    public void saveGameData(GameData gameData) {

    }
}
