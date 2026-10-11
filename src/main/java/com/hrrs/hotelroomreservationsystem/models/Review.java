package com.hrrs.hotelroomreservationsystem.models;

public abstract class Review extends Entity {
    private final String text;
    private final int rating;
    protected Review(long id, String text, int rating) {
        super(id); if (rating<1 || rating>5) throw new IllegalArgumentException("Rating must be 1–5");
        this.text=text; this.rating=rating;
    }
    public String getText() { return text; }
    public int getRating() { return rating; }
}
