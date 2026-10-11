package com.hrrs.hotelroomreservationsystem.models;

public abstract class Staff extends User {
    private final String shift;
    protected Staff(long id, String name, String email, String shift) { super(id,name,email); this.shift=shift; }
    public String getShift() { return shift; }
}
