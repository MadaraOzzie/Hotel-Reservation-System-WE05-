package com.hrrs.hotelroomreservationsystem.models;

public abstract class Review extends Entity {
    private final String text;
    private final int rating;

    protected Review(long id, String text, int rating) {
        super(id);
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
