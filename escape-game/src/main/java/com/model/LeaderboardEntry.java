package com.model;
import java.time.LocalTime;

/*
 * This class represents an entry in the leaderboard
 * @author We're Getting an A
 */
public class LeaderboardEntry {
    private User player;
    private int score;
    private LocalTime completionTime;

    /**
     * Constructor for LeaderboardEntry
     * @param player The user who completed the puzzle
     * @param score The score achieved by the user
     * @param completionTime The time taken to complete the puzzle
     */
    public LeaderboardEntry(User player, int score, LocalTime completionTime) {
        this.player = player;
        this.score = score;
        this.completionTime = completionTime;
    }

    /**
     * Compares this leaderboard entry to another entry.
     * @param other The other leaderboard entry to compare to
     * @return A negative integer, zero, or a positive integer as this entry is less than,
     * equal to, or greater than the specified entry
     */
    public int compareTo(LeaderboardEntry other) {
        if (this.score != other.score) {
            return Integer.compare(this.score, other.score);
        }
        return this.completionTime.compareTo(other.completionTime);
    }

    /**
     * Gets the time associated with this leaderboard entry
     * @return The completion time
     */
    public LocalTime getCompletionTime() {
    return completionTime;
    }

    /**
     * Gets the score associated with this leaderboard entry
     * @return The score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the player associated with this leaderboard entry
     * @return The user who completed the game
     */
    public User getPlayer() {
        return player;
    }
}
