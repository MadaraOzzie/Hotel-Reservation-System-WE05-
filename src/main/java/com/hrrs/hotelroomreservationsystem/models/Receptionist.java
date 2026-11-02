package com.hrrs.hotelroomreservationsystem.models;

public final class Receptionist extends Staff {
    public Receptionist(long id, String name, String email, String shift) {
        super(id, name, email, "RECEPTIONIST", shift);
    }
}
