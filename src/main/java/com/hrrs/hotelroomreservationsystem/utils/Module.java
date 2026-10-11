package com.hrrs.hotelroomreservationsystem.utils;

import java.util.List;
/** An allowlist: client-provided table/column names never enter SQL. */
public enum Module {
    GUESTS("guests", "name,email,phone,type,discount_rate"),
    ROOMS("rooms", "number,type,capacity,base_price,luxury_tax,status"),
    RESERVATIONS("reservations", "guest_id,room_id,check_in,check_out,party_size,type,status,total,cancellation_fee"),
    STAFF("staff", "name,email,phone,role,shift,password_hash"),
    SERVICES("services", "name,category,type,base_cost,status,description"),
    REVIEWS("reviews", "reservation_id,author,type,rating,text,status");
    private final String table;
    private final List<String> columns;
    Module(String table, String columns) { this.table=table; this.columns=List.of(columns.split(",")); }
    public String table() { return table; }
    public List<String> columns() { return columns; }
    public static Module from(String value) {
        try { return valueOf(value.toUpperCase(java.util.Locale.ROOT)); }
        catch (Exception e) { throw new IllegalArgumentException("Unknown module"); }
    }
}
