package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
public final class VIPGuest extends Guest {
    private final BigDecimal discountRate;
    public VIPGuest(long id, String name, String email, BigDecimal rate) {
        super(id, name, email);
        if (rate.signum() < 0 || rate.compareTo(new BigDecimal("0.50")) > 0) throw new IllegalArgumentException("Discount must be 0–50%");
        discountRate = rate;
    }
    public BigDecimal getDiscountRate() { return discountRate; }
    @Override public BigDecimal calculateDiscount(BigDecimal subtotal) { return subtotal.multiply(discountRate); }
    @Override public String getDisplayTag() { return "VIP guest"; }
}
