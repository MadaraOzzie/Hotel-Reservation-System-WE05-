package com.hrrs.hotelroomreservationsystem.models;

public final class AnonymousReview extends Review {
    public AnonymousReview(long id, String text, int rating) { super(id,text,rating); }
    @Override public String getDisplayTag() { return "Anonymous"; }
}
