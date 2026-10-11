package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
import java.time.LocalDate;
public final class WalkInBooking extends Reservation {
    public WalkInBooking(long id, long guest, long room, LocalDate in, LocalDate out) { super(id,guest,room,in,out); }
    @Override public BigDecimal calculateCancellationFee(BigDecimal total) { return total.multiply(new BigDecimal("0.00")).setScale(2,java.math.RoundingMode.HALF_UP); }
    @Override public String getDisplayTag() { return "Walk-in booking"; }
}
