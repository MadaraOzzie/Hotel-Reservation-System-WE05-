package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
public abstract class HotelService extends Entity {
    private final String serviceName;
    private final BigDecimal baseCost;
    protected HotelService(long id, String name, BigDecimal cost) { super(id); serviceName=name; baseCost=cost; }
    public String getServiceName() { return serviceName; }
    public BigDecimal getBaseCost() { return baseCost; }
    public abstract BigDecimal calculateCost();
}
