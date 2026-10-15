package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
public abstract class Reservation extends Entity {
    private final long guestId, roomId;
    private final LocalDate checkIn, checkOut;
    protected Reservation(long id, long guest, long room, LocalDate in, LocalDate out) {
        super(id); guestId=guest; roomId=room; checkIn=in; checkOut=out;
        if (!out.isAfter(in)) throw new IllegalArgumentException("Check-out must follow check-in");
    }
    public long getGuestId() { return guestId; }
    public long getRoomId() { return roomId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public long getNights() { return ChronoUnit.DAYS.between(checkIn,checkOut); }
    public BigDecimal calculateTotal(Room room, Guest guest) {
        BigDecimal subtotal = room.calculateFinalPrice().multiply(BigDecimal.valueOf(getNights()));
        return subtotal.subtract(guest.calculateDiscount(subtotal)).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    public abstract BigDecimal calculateCancellationFee(BigDecimal total);
}
