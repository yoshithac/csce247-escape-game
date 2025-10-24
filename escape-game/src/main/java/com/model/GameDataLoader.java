package com.model;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GameDataLoader {

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
	
    protected static final String USER_FILE_NAME = "src/main/resources/Users.json";
    protected static final String GAMEDATA_FILE_NAME = "src/main/resources/GameData.json";

    /**
     * Loads the list of user data.
     * @param usersFile
     */
    public List<User> readUsers() {
        String usersFile = USER_FILE_NAME;
        
        List<User> users = null;
        try {
            // FIXED: Use the configured gson instance instead of new Gson()
            users = gson.fromJson(
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
        }
        return users;
    }
    
    /**
     * Loads list of all game data (i.e. Leaderboards, puzzles, etc)
     * @return gameDataFile
     */
    public GameData readGameData() {
        String gameDataFile = GAMEDATA_FILE_NAME;

        GameData gameData = null;
        try {
            // FIXED: Use the configured gson instance instead of new Gson()
            gameData = gson.fromJson(
                new FileReader(gameDataFile),
                new TypeToken<GameData>() {}.getType()
            );
            
            // Check if gameData is null
            if (gameData == null) {
                System.out.println("Warning: JSON file is empty or returned null");
                gameData = new GameData(null, null, null, null, null);
            }
            
        } catch (Exception ex) {
            System.out.println("Could not find: " + gameDataFile);
            ex.printStackTrace();
        }
        return gameData;
    }

    // Custom adapter for LocalDateTime
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