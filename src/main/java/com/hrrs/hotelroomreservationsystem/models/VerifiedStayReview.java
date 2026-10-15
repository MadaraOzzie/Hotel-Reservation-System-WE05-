package com.hrrs.hotelroomreservationsystem.models;

public final class VerifiedStayReview extends Review {
    private final long reservationId;
    public VerifiedStayReview(long id, String text, int rating, long reservationId) { super(id,text,rating); this.reservationId=reservationId; }
    public long getReservationId() { return reservationId; }
    @Override public String getDisplayTag() { return "Verified stay"; }
}
