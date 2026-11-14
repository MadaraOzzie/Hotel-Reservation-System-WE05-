package com.hrrs.hotelroomreservationsystem.models;
// Class representing an anonymous customer review
public final class AnonymousReview extends Review {
    // Constructor to initialize the anonymous review details
    public AnonymousReview(long id, String text, int rating) {
        super(id, text, rating);
    }
   // Overrides the method to return the review type
    @Override public String getReviewType() { return "ANONYMOUS"; }
}
