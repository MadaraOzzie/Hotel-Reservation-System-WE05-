package com.hrrs.hotelroomreservationsystem.models;

// Class representing an anonymous 
//customer review
public final class AnonymousReview extends Review {

    // Constructor to initialize anonymous
    //review details
    public AnonymousReview(long id, String text, int rating) {

        // Calls the parent class constructor
        // Passes the ID, review text, and rating
        super(id, text, rating);
    }

    // Overrides the getDisplayTag method
    //from the parent class
    @Override
    public String getDisplayTag() {

        // Returns the display name of 
        //the review
        return "Anonymous";
    }
}
