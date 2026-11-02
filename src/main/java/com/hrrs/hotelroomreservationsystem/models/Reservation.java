package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Reservation extends Entity {
    private final long guestId;
    private final long roomId;
    private final LocalDate checkIn;
    private final LocalDate checkOut;

    protected Reservation(long id, long guestId, long roomId, LocalDate checkIn, LocalDate checkOut) {
        super(id);
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public long getGuestId() { return guestId; }
    public long getRoomId() { return roomId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public abstract String getBookingType();

    public BigDecimal calculateTotal(Room room, Guest guest) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal subtotal = room.calculateFinalPrice().multiply(BigDecimal.valueOf(Math.max(1L, nights)));
        BigDecimal discount = guest.calculateDiscount(subtotal);
        return subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateCancellationFee(BigDecimal total) {
        if ("ADVANCE".equals(getBookingType())) {
            return total.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDisplayTag() { return getBookingType() + " reservation"; }
}
