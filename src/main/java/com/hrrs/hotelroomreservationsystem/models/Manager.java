package com.hrrs.hotelroomreservationsystem.models;

public final class Manager extends Staff {
    public Manager(long id, String name, String email, String shift) {
        super(id, name, email, "MANAGER", shift);
    }
}
