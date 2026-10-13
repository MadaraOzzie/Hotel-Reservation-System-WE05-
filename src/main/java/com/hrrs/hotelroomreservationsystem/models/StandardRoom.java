package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
public final class StandardRoom extends Room {
    public StandardRoom(long id, String number, BigDecimal price) { super(id, number, price); }
    @Override public BigDecimal calculateFinalPrice() { return getBasePrice(); }
    @Override public String getDisplayTag() { return "Standard room"; }
}
