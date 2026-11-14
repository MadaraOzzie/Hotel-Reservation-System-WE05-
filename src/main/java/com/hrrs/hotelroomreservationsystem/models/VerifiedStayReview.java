package com.hrrs.hotelroomreservationsystem.models;

// Class representing a review from
//a customer with a verified stay
public final class VerifiedStayReview extends Review {

    // Stores the reservation ID related 
    //to the review
    private final long reservationId;

    // Constructor to initialize verified 
    //stay review details
    public VerifiedStayReview(long id, String text, int rating, long reservationId) {

        // Calls the parent class constructor
        // Passes the review ID, text, and rating
        super(id, text, rating);

        // Assigns the reservation ID 
        //to the variable
        this.reservationId = reservationId;
    }

    // Method to return the reservation ID
    public long getReservationId() {

        // Returns the stored reservation ID
        return reservationId;
    }

    // Overrides the getDisplayTag method 
    //from the parent class
    @Override
    public String getDisplayTag() {

        // Returns the display tag for
        //a verified stay review
        return "Verified stay";
    }
}
