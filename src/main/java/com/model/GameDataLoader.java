package com.model;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


/**
 * Loads data from JSON files using Gson
 * Reads from TWO JSON files:
 * 1. users.json - User accounts
 * 2. gamedata.json - Everything else
 */
public class GameDataLoader {
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
    private final String userFilePath;
    private final String gameDataFilePath;

    protected static final String USER_FILE_NAME = "src/main/resources/users.json";
    protected static final String GAMEDATA_FILE_NAME = "src/main/resources/gamedata.json";
    
    // Production constructor - uses default paths
    public GameDataLoader() {
        this(USER_FILE_NAME, GAMEDATA_FILE_NAME);
    }
    
    // Test constructor - allows custom paths
    public GameDataLoader(String userFilePath, String gameDataFilePath) {
        this.userFilePath = userFilePath;
        this.gameDataFilePath = gameDataFilePath;
    }


    /**
     * Loads the list of user data from users.json
     * @return List of User objects (never null)
     */
    public List<User> readUsers() {
        List<User> users = null;
        try {
            users = gson.fromJson(
                new FileReader(userFilePath),
                new TypeToken<List<User>>() {}.getType()
            );
            
            if (users == null) {
                System.out.println("Warning: users.json is empty or returned null");
                users = new ArrayList<>();
            }
        } catch (Exception ex) {
            System.out.println("Could not find or read: " + userFilePath);
            ex.printStackTrace();
            users = new ArrayList<>();
        }
        return users;
    }
    
    /**
     * Loads all game data from gamedata.json
     * @return GameData object containing all game data (never null)
     */
    public GameData readGameData() {
        GameData gameData = null;
        try {
            gameData = gson.fromJson(
                new FileReader(gameDataFilePath),
                GameData.class
            );
            
            if (gameData == null) {
                System.out.println("Warning: gamedata.json is empty or returned null");
                gameData = new GameData();
            }
        } catch (Exception ex) {
            System.out.println("Could not find or read: " + gameDataFilePath);
            ex.printStackTrace();
            gameData = new GameData();
        }
        return gameData;
    }
    
    /**
     * Custom adapter for LocalDateTime serialization/deserialization
     * Handles conversion between JSON string and LocalDateTime objects
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }
        
        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }
}

