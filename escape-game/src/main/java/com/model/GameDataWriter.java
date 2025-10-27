package com.model;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Writes data to JSON files using Gson
 * Writes to TWO JSON files:
 * 1. users.json - User accounts
 * 2. gamedata.json - Everything else
 */
public class GameDataWriter {
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
    
    protected static final String USER_FILE_NAME = "src/main/resources/users.json";
    protected static final String GAMEDATA_FILE_NAME = "src/main/resources/gamedata.json";
    
    /**
     * Writes the list of users to users.json
     * @param users The list of users to write
     * @return true if write was successful, false otherwise
     */
    public boolean writeUsers(List<User> users) {
        try (FileWriter writer = new FileWriter(USER_FILE_NAME)) {
            gson.toJson(users, writer);
            return true;
        } catch (IOException ex) {
            System.out.println("Could not write to: " + USER_FILE_NAME);
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Writes game data to gamedata.json
     * @param gameData The game data container to write
     * @return true if write was successful, false otherwise
     */
    public boolean writeGameData(GameData gameData) {
        try (FileWriter writer = new FileWriter(GAMEDATA_FILE_NAME)) {
            gson.toJson(gameData, writer);
            return true;
        } catch (IOException ex) {
            System.out.println("Could not write to: " + GAMEDATA_FILE_NAME);
            ex.printStackTrace();
            return false;
        }
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
