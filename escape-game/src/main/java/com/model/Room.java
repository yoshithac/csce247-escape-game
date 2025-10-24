package com.model;

public class Room {
    private String roomId;
    private String name;
    private String description;
    private boolean isCompleted;

    /**
     * Gets the room ID   
     * @return String roomId
     */
    public String getRoomId() { 
        return roomId; 
    }

    /**
     * Gets the room name
     * @return String name
     */
    public String getName() { 
        return name; 
    }

    /**
     * Gets the room description
     * @return String description
     */
    public String getDescription() { 
        return description; 
    }

    /**
     * Checks if the room is completed
     * @return boolean isCompleted
     */
    public boolean isCompleted() {
        return isCompleted; 
    }
}
