package com.hrrs.hotelroomreservationsystem.models;

import java.time.LocalDate;

public final class AdvanceBooking extends Reservation {
    public AdvanceBooking(long id, long guestId, long roomId, LocalDate checkIn, LocalDate checkOut) {
        super(id, guestId, roomId, checkIn, checkOut);
    }

    @Override public String getBookingType() { return "ADVANCE"; }
}
