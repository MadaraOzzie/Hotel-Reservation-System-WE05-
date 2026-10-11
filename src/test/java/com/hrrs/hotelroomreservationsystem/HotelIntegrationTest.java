package com.hrrs.hotelroomreservationsystem;

import com.hrrs.hotelroomreservationsystem.utils.*;
import com.hrrs.hotelroomreservationsystem.utils.Module;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.sql.*;
import java.math.BigDecimal;
import java.nio.file.Files;

/** Real SQL integration tests: independent in-memory database per test. */
class HotelIntegrationTest {
    private final HotelServiceLayer service=new HotelServiceLayer();
    private Map<String,Object> fields(Object... pairs) {
        Map<String,Object> result=new LinkedHashMap<>();
        for(int i=0;i<pairs.length;i+=2) result.put(pairs[i].toString(),pairs[i+1]);
        return result;
    }
    @BeforeEach void initialize() throws Exception { Database.useUrl("jdbc:h2:mem:"+UUID.randomUUID()+";DB_CLOSE_DELAY=-1");Database.initialize(); }
    private Map<String,Object> reservation(String in,String out) {
        return fields("guest_id","1","room_id","4","check_in",in,"check_out",out,"party_size","2","type","ADVANCE","status","CONFIRMED");
    }
    @Test void allSixModulesSupportCrudAndPersist() throws Exception {
        long guest=service.save(Module.GUESTS,0,fields("name","Test Guest","email","test@example.com","phone","0771234567","type","VIP","discount_rate","0.10"),1);
        long room=service.save(Module.ROOMS,0,fields("number","999","type","SUITE","capacity","4","base_price","10000","luxury_tax","0.10","status","AVAILABLE"),1);
        long staff=service.save(Module.STAFF,0,fields("name","Test Staff","email","teststaff@example.com","phone","0771234567","role","RECEPTIONIST","shift","NIGHT","password","TestPassword123"),1);
        long amenity=service.save(Module.SERVICES,0,fields("name","Test Amenity","category","DINING","type","PAID","base_cost","2000","status","ACTIVE","description","Test description"),1);
        Map<String,Object> booking=reservation("2026-09-01","2026-09-03");booking.put("guest_id",String.valueOf(guest));booking.put("room_id",String.valueOf(room));booking.put("status","CHECKED_OUT");
        long stay=service.save(Module.RESERVATIONS,0,booking,1);
        long review=service.save(Module.REVIEWS,0,fields("author","Test Guest","type","VERIFIED","reservation_id",String.valueOf(stay),"rating","4","text","Great stay","status","PUBLISHED"),1);
        for(var entry:Map.of(Module.GUESTS,guest,Module.ROOMS,room,Module.STAFF,staff,Module.SERVICES,amenity,Module.RESERVATIONS,stay,Module.REVIEWS,review).entrySet()) {
            var record=service.list(entry.getKey()).stream().filter(r->((Number)r.get("id")).longValue()==entry.getValue()).findFirst().orElseThrow();
            if(entry.getKey()==Module.RESERVATIONS)record.put("party_size","3");
            else if(entry.getKey()==Module.REVIEWS)record.put("text","Updated feedback");
            else if(entry.getKey()==Module.ROOMS)record.put("number","998");
            else record.put("name","Updated record "+entry.getKey());
            service.save(entry.getKey(),entry.getValue(),record,1);
        }
        assertEquals(new BigDecimal("19800.00"),service.list(Module.RESERVATIONS).stream().filter(r->((Number)r.get("id")).longValue()==stay).findFirst().orElseThrow().get("total"));
        for(var entry:List.of(Map.entry(Module.REVIEWS,review),Map.entry(Module.RESERVATIONS,stay),Map.entry(Module.GUESTS,guest),Map.entry(Module.ROOMS,room),Map.entry(Module.STAFF,staff),Map.entry(Module.SERVICES,amenity))) {
            service.delete(entry.getKey(),entry.getValue(),1);
            try(Connection c=Database.open()) {assertThrows(NoSuchElementException.class,()->new SqlRepository(c,entry.getKey()).findById(entry.getValue()));}
        }
    }
    @Test void overlapRejectedButAdjacentStayAllowed() throws Exception {
        assertThrows(IllegalArgumentException.class,()->service.save(Module.RESERVATIONS,0,reservation("2026-10-12","2026-10-15"),1));
        assertDoesNotThrow(()->service.save(Module.RESERVATIONS,0,reservation("2026-10-14","2026-10-16"),1));
    }
    @Test void cancelledBookingReleasesRoomAndCalculatesFee() throws Exception {
        var booking=service.list(Module.RESERVATIONS).stream().filter(r->((Number)r.get("id")).longValue()==1).findFirst().orElseThrow();
        booking.put("status","CANCELLED");service.save(Module.RESERVATIONS,1,booking,1);
        assertEquals(new BigDecimal("12474.00"),service.list(Module.RESERVATIONS).stream().filter(r->((Number)r.get("id")).longValue()==1).findFirst().orElseThrow().get("cancellation_fee"));
        assertDoesNotThrow(()->service.save(Module.RESERVATIONS,0,reservation("2026-10-11","2026-10-14"),1));
    }
    @Test void invalidDatesAndCapacityAreRejected() {
        assertThrows(IllegalArgumentException.class,()->service.save(Module.RESERVATIONS,0,reservation("2026-11-05","2026-11-05"),1));
        var record=reservation("2026-11-01","2026-11-03");record.put("party_size","5");
        assertThrows(IllegalArgumentException.class,()->service.save(Module.RESERVATIONS,0,record,1));
    }
    @Test void foreignKeysAndSelfProtection() {
        assertThrows(SQLException.class,()->service.delete(Module.GUESTS,1,1));
        assertThrows(IllegalArgumentException.class,()->service.delete(Module.STAFF,1,1));
        assertThrows(IllegalArgumentException.class,()->service.delete(Module.RESERVATIONS,1,1));
    }
    @Test void verifiedReviewRequiresCompletedStay() {
        assertThrows(IllegalArgumentException.class,()->service.save(Module.REVIEWS,0,fields("author","Guest","type","VERIFIED","reservation_id","1","rating","5","text","Great","status","PUBLISHED"),1));
    }
    @Test void passwordsAreSaltedAndNeverReturned() throws Exception {
        String a=Passwords.hash("Secret12345"),b=Passwords.hash("Secret12345");assertNotEquals(a,b);
        assertTrue(Passwords.verify("Secret12345",a));assertFalse(Passwords.verify("incorrect",a));
        assertFalse(service.list(Module.STAFF).get(0).containsKey("password_hash"));
    }
    @Test void allModuleFilesRoundTrip() throws Exception {
        for(Module m:Module.values()) {
            FileManager files=new FileManager(m);var rows=service.list(m);var path=files.writeSnapshot(rows);
            assertEquals(rows.size(),files.readSnapshot(path).size());assertFalse(Files.readString(path).contains("password_hash"));
        }
    }
    @Test void duplicateEmailRollsBack() throws Exception {
        int before=service.list(Module.GUESTS).size();
        assertThrows(SQLException.class,()->service.save(Module.GUESTS,0,fields("name","Duplicate","email","amara@example.com","phone","0771234567","type","STANDARD","discount_rate","0"),1));
        assertEquals(before,service.list(Module.GUESTS).size());
    }
    @Test void complimentaryServicesChargeZero() throws Exception {
        long id=service.save(Module.SERVICES,0,fields("name","Free test","category","LEISURE","type","COMPLIMENTARY","base_cost","500","status","ACTIVE","description","Included"),1);
        var row=service.list(Module.SERVICES).stream().filter(r->((Number)r.get("id")).longValue()==id).findFirst().orElseThrow();
        assertEquals(0,new BigDecimal(row.get("effective_cost").toString()).signum());
    }
}
