package com.model;

import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GameDataLoader {
	
	protected static final String USER_FILE_NAME = "escape-game/src/main/resources/Users.json";
    protected static final String GAMEDATA_FILE_NAME = "escape-game/src/main/resources/GameData.json";

    /**
	 * Loads the list of user data.
	 * @param usersFile
	 */
	public List<User> readUsers() {
    String usersFile = USER_FILE_NAME;
    
    List<User> users = null;
    try {
        users = new Gson().fromJson(
            new FileReader(usersFile),
            new TypeToken<List<User>>() {}.getType()
        );
        
        // Check if users is null
        if (users == null) {
            System.out.println("Warning: JSON file is empty or returned null");
            users = new java.util.ArrayList<>(); // Return empty list instead of null
        }
        
    } catch (Exception ex) {
        System.out.println("Could not find: " + usersFile);
        ex.printStackTrace();
        //throw new Exception("File not found: Users file not found", ex);
    }
    return users;
}
/**
 * Loads list of all game data (i.e. Leaderboeards, puzzles, etc)
 * @return gameDataFile
 */
public GameData readGameData() {
String gameDataFile = GAMEDATA_FILE_NAME;

GameData gameData = null;
try {
        gameData = new Gson().fromJson(
            new FileReader(gameDataFile),
            new TypeToken<GameData>() {}.getType()
        );
        
        // Check if gameData is null
        if (gameData == null) {
            System.out.println("Warning: JSON file is empty or returned null");
            gameData = new GameData(null, null, null, null, null); // Return empty object instead of null
        }
        
    } catch (Exception ex) {
        System.out.println("Could not find: " + gameDataFile);
        ex.printStackTrace();
        //throw new Exception("File not found: Users file not found", ex);
    }
    return gameData;
}
}