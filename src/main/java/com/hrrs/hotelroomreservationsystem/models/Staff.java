package com.hrrs.hotelroomreservationsystem.models;

public class Staff extends User {
    private final String role;
    private final String shift;

    public Staff(long id, String name, String email, String role, String shift) {
        super(id, name, email);
        this.role = role;
        this.shift = shift;
    }

    public String getRole() { return role; }
    public String getShift() { return shift; }
    @Override public String getAccessLevel() { return role; }
    @Override public String getDisplayTag() { return role + " staff"; }
}
