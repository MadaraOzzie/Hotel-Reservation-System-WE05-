package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;

public abstract class HotelService extends Entity {
    private final String name;
    private final BigDecimal baseCost;

    protected HotelService(long id, String name, BigDecimal baseCost) {
        super(id);
        this.name = name;
        this.baseCost = baseCost;
    }

    public String getName() { return name; }
    public BigDecimal getBaseCost() { return baseCost; }
    public abstract String getServiceType();
    public abstract BigDecimal calculateCost();
    public abstract String getCategory();

    @Override
    public String getDisplayTag() { return getServiceType(); }
}
