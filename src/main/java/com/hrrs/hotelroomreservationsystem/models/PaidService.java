package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
public final class PaidService extends HotelService {
    public PaidService(long id, String name, BigDecimal cost) { super(id,name,cost); }
    @Override public BigDecimal calculateCost() { return getBaseCost(); }
    @Override public String getDisplayTag() { return "Paid amenity"; }
}
