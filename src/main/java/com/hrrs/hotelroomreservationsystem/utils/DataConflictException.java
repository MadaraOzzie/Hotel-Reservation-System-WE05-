package com.hrrs.hotelroomreservationsystem.utils;

/** Thrown when a file-backed write would break a unique or link constraint. */
public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
