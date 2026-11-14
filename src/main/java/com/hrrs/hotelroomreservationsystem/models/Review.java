package com.hrrs.hotelroomreservationsystem.models;

// Abstract class representing a review
public abstract class Review extends Entity {
    // Stores the review text
    private final String text;
    // Stores the rating given by the customer
    private final int rating;
    // Constructor to initialize review details

    protected Review(long id, String text, int rating) {
        super(id);// Calls the parent class constructor
        this.text = text;
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public abstract String getReviewType();

    @Override
    public String getDisplayTag() {
        return getReviewType() + " review";
    }
}
