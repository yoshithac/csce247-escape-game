package com.model;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;

public class GameDataLoader {
	
	protected static final String USER_FILE_NAME = "escape-game/src/main/resources/Users.json";



    /**
	 * Loads the list of system usernames and permissions.
	 * @param usersFile
	 * @throws Exception
	 */
	public List<User> loadUsers() throws Exception {
    String usersFile = USER_FILE_NAME;
    
    List<User> users;
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
        throw new Exception("File not found: Users file not found", ex);
    }
    return users;
}
}