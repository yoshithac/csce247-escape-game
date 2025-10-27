package com.model;

/**
 * Card entity for matching game
 */
public class Card {
    private String id;
    private String value;  // emoji or text
    private String name;
    
    /**
     * Default constructor that creates an empty card
     */
    public Card() {}

    /**
     *Constructs a Card object with the specified ID, value, and name.
     * 
     * @param id the unique identifier for the card
     * @param value the display value of the card 
     * @param name the descriptive name of the card
     */
    public Card(String id, String value, String name) {
        this.id = id;
        this.value = value;
        this.name = name;
    }
    
// Getters and Setters
    /**
     * Gets the unique identifier of the card.
     *
     * @return the card's ID
     */
    public String getId() { return id; }

    /**
     * Sets the unique identifier of the card.
     *
     * @param id the card's ID
     */
    public void setId(String id) { this.id = id; }
    
    /**
     * Gets the display value of the card
     *
     * @return the card's display value
     */
    public String getValue() { return value; }

    /**
     * Sets the display value of the card.
     *
     * @param value the new display value 
     */
    public void setValue(String value) { this.value = value; }
    
    /**
     * Gets the name of the card.
     *
     * @return the card's name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the card.
     *
     * @param name the new name of the card
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * Returns a string representation of the card, including its ID, name, and value.
     *
     * @return a formatted string describing the card
     */
    @Override
    public String toString() {
        return String.format("Card[%s: %s (%s)]", id, name, value);
    }
}