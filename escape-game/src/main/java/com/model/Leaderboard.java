package com.model;
import java.util.List;
import java.time.LocalTime;

/**
 * This class represents the leaderboard for the game
 * @author We're Getting an A
 */
public class Leaderboard {
    private static Leaderboard leaderboard;
    private List<LeaderboardEntry> entries;

    /**
     * Singleton instance of Leaderboard
     * @return the single instance of Leaderboard
    */
    public static Leaderboard getInstance() {
        if (leaderboard == null) {
            leaderboard = new Leaderboard();
        }
        return leaderboard;
    }

    /**
     * Adds a new entry to the leaderboard.
     * @param user The user who completed the puzzle
     * @param score The score achieved by the user
     * @param time The time taken to complete the puzzle
     */
    public void addEntry(User user, int score, int time) {
        LeaderboardEntry entry = new LeaderboardEntry(user, score, LocalTime.ofSecondOfDay(time));
        entries.add(entry);
    }

    /**
     * Sorts the leaderboard entries by completion time.
     * @return A list of leaderboard entries sorted by completion time
     */
    public List<LeaderboardEntry> sortByTime() {
        return entries;
    }

    /**
     * Sorts the leaderboard entries by score.
     * @return A list of leaderboard entries sorted by score
     */
    public List<LeaderboardEntry> sortByScore() {
        return entries;
    }

    /**
     * Sorts the leaderboard entries by player name.
     * @return A list of leaderboard entries sorted by player name
     */
    public List<LeaderboardEntry> sortByName() {
        return entries;
    }
}
