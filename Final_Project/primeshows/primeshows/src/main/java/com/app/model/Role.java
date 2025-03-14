package com.app.model;

public enum Role {
    
    USER("Standard User - Can book shows and manage bookings"),
    ADMIN("Administrator - Manages platform, users, and analytics"),
    SHOW_ORGANIZER("Show Organizer - Can create and manage shows");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.name() + " - " + description;
    }
}
