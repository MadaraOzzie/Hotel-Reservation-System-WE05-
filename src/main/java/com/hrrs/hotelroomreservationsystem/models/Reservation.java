package com.hrrs.hotelroomreservationsystem.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Abstract base class for all hotel reservations, inheriting unique ID from Entity
public abstract class Reservation extends Entity {
    // Immutable reservation details linking a guest and room for specific stay dates
    private final long guestId;
    private final long roomId;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    
// Initializes shared reservation attributes and passes ID to the parent Entity class
    protected Reservation(long id, long guestId, long roomId, LocalDate checkIn, LocalDate checkOut) {
        super(id);
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
    // Standard getter methods for encapsulated reservation fields
    public long getGuestId() { return guestId; }
    public long getRoomId() { return roomId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    // Implemented by concrete subclasses (AdvanceBooking or WalkInBooking)
    public abstract String getBookingType();
    
   // Calculates final stay cost using room rate, number of nights, and guest discount
    public BigDecimal calculateTotal(Room room, Guest guest) {
        // Count total nights between check-in and check-out
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        // Enforce a minimum charge of 1 night and multiply by room's final nightly price
        BigDecimal subtotal = room.calculateFinalPrice().multiply(BigDecimal.valueOf(Math.max(1L, nights)));
        // Enforce a minimum charge of 1 night and multiply by room's final nightly price
        BigDecimal discount = guest.calculateDiscount(subtotal);
        // Subtract discount and round to 2 decimal places for accurate currency formatting
        return subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
   // Calculates cancellation penalty based on booking type (10% for ADVANCE, free for WALK_IN)
    public BigDecimal calculateCancellationFee(BigDecimal total) {
        if ("ADVANCE".equals(getBookingType())) {
            return total.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    // Returns a readable label combining the booking type for UI display
    @Override
    public String getDisplayTag() { return getBookingType() + " reservation"; }
}
