package com.hrrs.hotelroomreservationsystem.utils;

import com.hrrs.hotelroomreservationsystem.models.*;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;
public final class DomainFactory {
    private DomainFactory() {}
    private static String s(Map<String,Object> row,String key) { return String.valueOf(row.get(key)); }
    private static long id(Map<String,Object> row) { return Long.parseLong(s(row,"id")); }
    private static BigDecimal d(Map<String,Object> row,String key) { return new BigDecimal(s(row,key)); }
    public static Guest guest(Map<String,Object> r) {
        return "VIP".equals(s(r,"type")) ? new VIPGuest(id(r),s(r,"name"),s(r,"email"),d(r,"discount_rate")) : new Guest(id(r),s(r,"name"),s(r,"email"));
    }
    public static Room room(Map<String,Object> r) {
        return "SUITE".equals(s(r,"type")) ? new SuiteRoom(id(r),s(r,"number"),d(r,"base_price"),d(r,"luxury_tax")) : new StandardRoom(id(r),s(r,"number"),d(r,"base_price"));
    }
    public static Reservation reservation(Map<String,Object> r) {
        long id=r.containsKey("id")?id(r):0, guest=Long.parseLong(s(r,"guest_id")),room=Long.parseLong(s(r,"room_id"));
        LocalDate in=LocalDate.parse(s(r,"check_in")),out=LocalDate.parse(s(r,"check_out"));
        return "ADVANCE".equals(s(r,"type")) ? new AdvanceBooking(id,guest,room,in,out) : new WalkInBooking(id,guest,room,in,out);
    }
    public static Staff staff(Map<String,Object> r) {
        return "MANAGER".equals(s(r,"role")) ? new Manager(id(r),s(r,"name"),s(r,"email"),s(r,"shift")) : new Receptionist(id(r),s(r,"name"),s(r,"email"),s(r,"shift"));
    }
    public static HotelService service(Map<String,Object> r) {
        return "PAID".equals(s(r,"type")) ? new PaidService(id(r),s(r,"name"),d(r,"base_cost")) : new ComplimentaryService(id(r),s(r,"name"),d(r,"base_cost"));
    }
    public static Review review(Map<String,Object> r) {
        int rating=Integer.parseInt(s(r,"rating"));
        return "VERIFIED".equals(s(r,"type")) ? new VerifiedStayReview(id(r),s(r,"text"),rating,Long.parseLong(s(r,"reservation_id"))) : new AnonymousReview(id(r),s(r,"text"),rating);
    }
    public static Entity entity(Module m,Map<String,Object> r) {
        return switch(m) { case GUESTS->guest(r); case ROOMS->room(r); case RESERVATIONS->reservation(r); case STAFF->staff(r); case SERVICES->service(r); case REVIEWS->review(r); };
    }
}
