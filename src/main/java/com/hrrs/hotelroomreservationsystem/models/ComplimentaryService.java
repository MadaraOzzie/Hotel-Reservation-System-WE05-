package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;

public final class ComplimentaryService extends HotelService {
    public ComplimentaryService(long id, String name, BigDecimal cost) {
        super(id, name, cost);
    }

    @Override
    public BigDecimal calculateCost() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getDisplayTag() {
        return "Included amenity";
    }
}
