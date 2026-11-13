package com.hrrs.hotelroomreservationsystem.models;

/**
 * Abstract classs for ID variable:D
 */
public abstract class Entity {
    private long id;

    protected Entity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id < 0) throw new IllegalArgumentException("Invalid ID");
        this.id = id;
    }

    public abstract String getDisplayTag();
}
