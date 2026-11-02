package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;

public final class ComplimentaryService extends HotelService {
    private final String category;

    public ComplimentaryService(long id, String name, BigDecimal baseCost, String category) {
        super(id, name, baseCost);
        this.category = category;
    }

    public ComplimentaryService(long id, String name, BigDecimal baseCost) {
        this(id, name, baseCost, "DINING");
    }

    @Override
    public String getServiceType() {
        return "COMPLIMENTARY";
    }

    @Override
    public BigDecimal calculateCost() {
        return BigDecimal.ZERO.setScale(2);
    }

    @Override
    public String getCategory() {
        return category;
    }
}
