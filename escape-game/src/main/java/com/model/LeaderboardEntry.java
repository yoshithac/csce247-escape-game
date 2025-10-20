package com.model;

/*
 * This class represents an entry in the leaderboard
 * @author We're Getting an A
 */
public class LeaderboardEntry {
    private User player;
    private int score;
    public int completionTime;

    /**
     * Constructor for LeaderboardEntry
     * @param player The user who completed the puzzle
     * @param score The score achieved by the user
     * @param completionTime The time taken to complete the puzzle
     */
    public LeaderboardEntry(User player, int score, int completionTime) {
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
    public boolean  compareTo(LeaderboardEntry other) {
        return false;
    }

    /**
     * Gets the time associated with this leaderboard entry
     * @return The completion time
     */
    public int getCompletionTime() {
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

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("| player=").append(player.getUserId());
        sb.append(", score=").append(score);
        sb.append(", completionTime=").append(completionTime);
        sb.append(" |");
        
        return sb.toString();
    }
}
