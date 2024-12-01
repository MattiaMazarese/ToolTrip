package com.example.tooltrip;

public class Item {
    private String itemId;
    private String name;
    private String description;
    private String availability;

    public Item() {
        // Costruttore vuoto richiesto da Firebase
    }

    public Item(String itemId, String name, String description, String availability) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.availability = availability;
    }

    // Getter e Setter
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
