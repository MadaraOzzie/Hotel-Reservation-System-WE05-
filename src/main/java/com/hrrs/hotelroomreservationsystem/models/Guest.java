package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;

public class Guest extends User {
    public Guest(long id, String name, String email) {
        super(id, name, email);
    }

    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        return BigDecimal.ZERO;
    }

    @Override
    public String getAccessLevel() {
        return "GUEST";
    }

    @Override
    public String getDisplayTag() {
        return "Standard guest";
    }
}
