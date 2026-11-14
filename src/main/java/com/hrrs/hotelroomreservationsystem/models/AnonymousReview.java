package com.hrrs.hotelroomreservationsystem.models;
// Class representing an anonymous customer review
public final class AnonymousReview extends Review {
    public AnonymousReview(long id, String text, int rating) {
        super(id, text, rating);
    }

    @Override public String getReviewType() { return "ANONYMOUS"; }
}
