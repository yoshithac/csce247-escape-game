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

public class GameDataWriter {

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
    
    protected static final String USER_FILE_NAME = "src/main/resources/Users1.json";
    protected static final String GAMEDATA_FILE_NAME = "src/main/resources/GameData1.json";

    /**
     * Writes the list of user data to file.
     * @param users The list of users to write
     * @return true if write was successful, false otherwise
     */
    public boolean writeUsers(List<User> users) {
        String usersFile = USER_FILE_NAME;
        
        try (FileWriter writer = new FileWriter(usersFile)) {
            gson.toJson(users, writer);
            return true;
        } catch (IOException ex) {
            System.out.println("Could not write to: " + usersFile);
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Writes game data (i.e. Leaderboards, puzzles, etc) to file.
     * @param gameData The game data to write
     * @return true if write was successful, false otherwise
     */
    public boolean writeGameData(GameData gameData) {
        String gameDataFile = GAMEDATA_FILE_NAME;
        
        try (FileWriter writer = new FileWriter(gameDataFile)) {
            gson.toJson(gameData, writer);
            return true;
        } catch (IOException ex) {
            System.out.println("Could not write to: " + gameDataFile);
            ex.printStackTrace();
            return false;
        }
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