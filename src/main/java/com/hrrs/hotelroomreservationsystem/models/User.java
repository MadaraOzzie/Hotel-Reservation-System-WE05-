package com.hrrs.hotelroomreservationsystem.models;

// Abstract class that derives from the class Entity to get name and email variables

public abstract class User extends Entity {
    private final String name;
    private final String email;

    protected User(long id, String name, String email) {
        super(id);
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public abstract String getAccessLevel();
}
