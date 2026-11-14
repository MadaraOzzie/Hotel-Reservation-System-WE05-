package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;

public abstract class Room extends Entity {
    private final String roomNumber;
    private final BigDecimal basePrice;

    protected Room(long id, String number, BigDecimal price) {
        super(id);
        roomNumber = number;
        basePrice = price;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public abstract BigDecimal calculateFinalPrice();
}
