package com.model;

/**
 * Card entity for matching game
 */
public class Card {
    private String id;
    private String value;  // emoji or text
    private String name;
    
    // Constructors
    public Card() {}
    
    public Card(String id, String value, String name) {
        this.id = id;
        this.value = value;
        this.name = name;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return String.format("Card[%s: %s (%s)]", id, name, value);
    }
}
