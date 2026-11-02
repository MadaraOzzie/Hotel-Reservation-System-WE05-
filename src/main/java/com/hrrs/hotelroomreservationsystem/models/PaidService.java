package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;

public final class PaidService extends HotelService {
    private final String category;

    public PaidService(long id, String name, BigDecimal baseCost, String category) {
        super(id, name, baseCost);
        this.category = category;
    }

    public PaidService(long id, String name, BigDecimal baseCost) {
        this(id, name, baseCost, "DINING");
    }

    @Override public String getServiceType() { return "PAID"; }
    @Override public BigDecimal calculateCost() { return getBaseCost(); }
    @Override public String getCategory() { return category; }
}
