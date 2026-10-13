package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
public final class SuiteRoom extends Room {
    private final BigDecimal luxuryTax;
    public SuiteRoom(long id, String number, BigDecimal price, BigDecimal tax) { super(id, number, price); luxuryTax = tax; }
    public BigDecimal getLuxuryTax() { return luxuryTax; }
    @Override public BigDecimal calculateFinalPrice() { return getBasePrice().multiply(BigDecimal.ONE.add(luxuryTax)); }
    @Override public String getDisplayTag() { return "Suite"; }
}
