package com.hrrs.hotelroomreservationsystem.models;

import java.time.LocalDate;

// Concrete final subclass representing a pre-scheduled advance reservation
public final class AdvanceBooking extends Reservation {
    // Constant identifier used for advance booking type checks and cancellation rules
    public static final String BOOKING_TYPE = "ADVANCE";

    // Passes booking details up to the abstract Reservation parent constructor
    public AdvanceBooking(long id, long guestId, long roomId, LocalDate checkIn, LocalDate checkOut) {
        super(id, guestId, roomId, checkIn, checkOut);
    }

    // Returns ADVANCE so Reservation.calculateCancellationFee applies the 10% cancellation fee
    @Override
    public String getBookingType() {
        return BOOKING_TYPE;
    }

    // Indicates whether cancelling this booking type incurs a penalty fee
    public boolean hasCancellationPenalty() {
        return true;
    }
}
