package com.hrrs.hotelroomreservationsystem.models;

// Abstract class representing a customer review
public abstract class Review extends Entity {

    // Variable to store the review text
    private final String text;

    // Variable to store the customer rating
    private final int rating;

    // Constructor to initialize review details
    protected Review(long id, String text, int rating) {

        // Calls the constructor of the parent class
        super(id);

        // Checks whether the rating is between 1 and 5
        if (rating < 1 || rating > 5) {

            // Throws an exception if the rating is invalid
            throw new IllegalArgumentException("Rating must be 1–5");
        }

        // Assigns the review text to the variable
        this.text = text;

        // Assigns the rating to the variable
        this.rating = rating;
    }

    // Method to return the review text
    public String getText() {

        // Returns the stored review text
        return text;
    }

    // Method to return the customer rating
    public int getRating() {

        // Returns the stored rating value
        return rating;
    }
}
