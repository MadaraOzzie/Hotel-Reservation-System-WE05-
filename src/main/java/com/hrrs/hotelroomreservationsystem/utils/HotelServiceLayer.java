package com.hrrs.hotelroomreservationsystem.utils;

import com.hrrs.hotelroomreservationsystem.models.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
/** Business rules and storage boundaries, independent of HTTP and JSP. */
public final class HotelServiceLayer {
    // Serializes local writes, including overlap checks, before replacing JSON files.
    private static final Object WRITE_LOCK=new Object();
    public List<Map<String,Object>> list(Module m) {
        List<Map<String,Object>> rows=new FileRepository(m).findAll();
        for(Map<String,Object> r:rows) {
            r.put("display_tag",DomainFactory.entity(m,r).getDisplayTag());
            if(m==Module.ROOMS) r.put("nightly_price",DomainFactory.room(r).calculateFinalPrice());
            if(m==Module.SERVICES) r.put("effective_cost",DomainFactory.service(r).calculateCost());
            r.remove("password_hash");
        }
        return rows;
    }
    private String text(Map<String,Object> row,String key,int max) {
        Object raw=row.get(key); String value=raw==null?"":raw.toString().trim();
        if(value.isBlank() || value.length()>max) throw new IllegalArgumentException(key.replace('_',' ')+" is required (maximum "+max+" characters)");
        row.put(key,value); return value;
    }
    private String choice(Map<String,Object> row,String key,String... choices) {
        String value=text(row,key,30); if(!List.of(choices).contains(value)) throw new IllegalArgumentException("Invalid "+key); return value;
    }
    private BigDecimal decimal(Map<String,Object> row,String key,String min,String max) {
        try {
            BigDecimal d=new BigDecimal(String.valueOf(row.get(key)));
            if(d.compareTo(new BigDecimal(min))<0 || d.compareTo(new BigDecimal(max))>0) throw new NumberFormatException();
            row.put(key,d); return d;
        } catch(Exception e) { throw new IllegalArgumentException(key.replace('_',' ')+" must be between "+min+" and "+max); }
    }
    private long integer(Map<String,Object> row,String key,long min,long max) {
        try { long value=Long.parseLong(String.valueOf(row.get(key))); if(value<min || value>max) throw new NumberFormatException(); row.put(key,value); return value; }
        catch(Exception e) { throw new IllegalArgumentException("Invalid "+key.replace('_',' ')); }
    }
    private void contact(Map<String,Object> r) {
        text(r,"name",100); String email=text(r,"email",160).toLowerCase(Locale.ROOT);
        if(!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) throw new IllegalArgumentException("Enter a valid email address");
        r.put("email",email); String phone=text(r,"phone",30);
        if(!phone.matches("[+0-9 ()-]{7,30}")) throw new IllegalArgumentException("Enter a valid phone number");
    }
    private boolean hasActiveBooking(String column,long id) {
        return new FileRepository(Module.RESERVATIONS).findAll().stream().anyMatch(row->
                sameId(row.get(column),id) && List.of("CONFIRMED","CHECKED_IN").contains(String.valueOf(row.get("status"))));
    }
    private void validate(Module m,long id,Map<String,Object> r,long actor) {
        switch(m) {
            case GUESTS -> {
                contact(r); String type=choice(r,"type","STANDARD","VIP"); decimal(r,"discount_rate","0","0.5");
                if(type.equals("STANDARD")) r.put("discount_rate",BigDecimal.ZERO);
            }
            case ROOMS -> {
                text(r,"number",12); String type=choice(r,"type","STANDARD","SUITE"); integer(r,"capacity",1,12);
                decimal(r,"base_price","0.01","10000000"); decimal(r,"luxury_tax","0","0.3");
                if(type.equals("STANDARD")) r.put("luxury_tax",BigDecimal.ZERO);
                String status=choice(r,"status","AVAILABLE","MAINTENANCE");
                if(id>0 && hasActiveBooking("room_id",id)) {
                    Map<String,Object> old=new FileRepository(m).findById(id);
                    if(status.equals("MAINTENANCE")) throw new IllegalArgumentException("Cancel or complete active reservations before maintenance");
                    if(Long.parseLong(r.get("capacity").toString())<Long.parseLong(old.get("capacity").toString()))
                        throw new IllegalArgumentException("Room capacity cannot decrease while reservations are active");
                }
            }
            case STAFF -> {
                contact(r); choice(r,"role","MANAGER","RECEPTIONIST"); choice(r,"shift","MORNING","EVENING","NIGHT");
                if(id==actor && !"MANAGER".equals(r.get("role"))) throw new IllegalArgumentException("You cannot remove your own manager access");
                String password=Objects.toString(r.remove("password"),""); r.remove("password_hash");
                if(id==0 || !password.isBlank()) {
                    if(password.length()<10 || password.length()>128) throw new IllegalArgumentException("Password must be 10–128 characters");
                    r.put("password_hash",Passwords.hash(password));
                }
            }
            case SERVICES -> {
                text(r,"name",100); text(r,"description",1000); choice(r,"category","DINING","WELLNESS","TRANSPORT","LEISURE");
                String type=choice(r,"type","PAID","COMPLIMENTARY"); decimal(r,"base_cost","0","10000000");
                choice(r,"status","ACTIVE","UNAVAILABLE"); if(type.equals("COMPLIMENTARY")) r.put("base_cost",BigDecimal.ZERO);
            }
            case RESERVATIONS -> {
                long guest=integer(r,"guest_id",1,Long.MAX_VALUE),room=integer(r,"room_id",1,Long.MAX_VALUE);
                long party=integer(r,"party_size",1,12); choice(r,"type","ADVANCE","WALK_IN");
                String status=choice(r,"status","CONFIRMED","CHECKED_IN","CHECKED_OUT","CANCELLED");
                LocalDate in,out;
                try {in=LocalDate.parse(text(r,"check_in",10));out=LocalDate.parse(text(r,"check_out",10));}
                catch(java.time.DateTimeException e) {throw new IllegalArgumentException("Enter valid stay dates");}
                if(!out.isAfter(in) || out.isAfter(in.plusDays(365))) throw new IllegalArgumentException("Stay must be between 1 and 365 nights");
                Map<String,Object> roomRow=new FileRepository(Module.ROOMS).findById(room);
                Map<String,Object> guestRow=new FileRepository(Module.GUESTS).findById(guest);
                if(party>Integer.parseInt(roomRow.get("capacity").toString())) throw new IllegalArgumentException("Party exceeds room capacity");
                boolean active=List.of("CONFIRMED","CHECKED_IN").contains(status);
                if(active && !"AVAILABLE".equals(roomRow.get("status"))) throw new IllegalArgumentException("Room is under maintenance");
                if(active) for(Map<String,Object> other:new FileRepository(Module.RESERVATIONS).findAll()) {
                    if(sameId(other.get("id"),id) || !sameId(other.get("room_id"),room)) continue;
                    if(!List.of("CONFIRMED","CHECKED_IN").contains(String.valueOf(other.get("status")))) continue;
                    LocalDate otherIn=LocalDate.parse(String.valueOf(other.get("check_in")));
                    LocalDate otherOut=LocalDate.parse(String.valueOf(other.get("check_out")));
                    if(in.isBefore(otherOut) && out.isAfter(otherIn)) throw new IllegalArgumentException("Room already reserved for these dates");
                }
                if(status.equals("CHECKED_IN") && (LocalDate.now().isBefore(in) || !LocalDate.now().isBefore(out))) throw new IllegalArgumentException("Check-in is only available during the stay dates");
                if(status.equals("CHECKED_OUT") && LocalDate.now().isBefore(in)) throw new IllegalArgumentException("Cannot check out before arrival");
                // A confirmed quote is a snapshot: unchanged dates/guest/room/type retain the agreed price.
                Reservation booking=DomainFactory.reservation(r);
                BigDecimal total=booking.calculateTotal(DomainFactory.room(roomRow),DomainFactory.guest(guestRow));
                if(id>0) {
                    Map<String,Object> old=new FileRepository(m).findById(id);
                    boolean same=List.of("guest_id","room_id","check_in","check_out","type").stream().allMatch(k->Objects.toString(old.get(k)).equals(Objects.toString(r.get(k))));
                    if(same) total=new BigDecimal(old.get("total").toString());
                    if(!status.equals("CHECKED_OUT") && new FileRepository(Module.REVIEWS).findAll().stream().anyMatch(x->sameId(x.get("reservation_id"),id)))
                        throw new IllegalArgumentException("Remove linked verified reviews before changing completed stay status");
                }
                r.put("total",total); r.put("cancellation_fee",status.equals("CANCELLED")?booking.calculateCancellationFee(total):BigDecimal.ZERO);
            }
            case REVIEWS -> {
                text(r,"author",100); text(r,"text",2000); integer(r,"rating",1,5); choice(r,"status","PUBLISHED","HIDDEN");
                String type=choice(r,"type","VERIFIED","ANONYMOUS");
                if(type.equals("VERIFIED")) {
                    long booking=integer(r,"reservation_id",1,Long.MAX_VALUE);
                    Map<String,Object> stay=new FileRepository(Module.RESERVATIONS).findById(booking);
                    if(!"CHECKED_OUT".equals(stay.get("status"))) throw new IllegalArgumentException("Verified reviews require a checked-out reservation");
                } else {r.put("reservation_id",null);r.put("author","Anonymous");}
            }
        }
    }
    public long save(Module m,long id,Map<String,Object> input,long actor) {
        synchronized(WRITE_LOCK) {
            if(id>0) new FileRepository(m).findById(id);
            Map<String,Object> r=new LinkedHashMap<>();
            for(String column:m.columns()) if(input.containsKey(column)) r.put(column,input.get(column));
            if(m==Module.STAFF) r.put("password",input.get("password"));
            validate(m,id,r,actor);
            long result=new FileRepository(m).save(id,r);
            log(actor+" "+(id==0?"created ":"updated ")+m.table()+" #"+result);
            return result;
        }
    }
    public void delete(Module m,long id,long actor) {
        synchronized(WRITE_LOCK) {
            Map<String,Object> row=new FileRepository(m).findById(id);
            if(m==Module.STAFF && id==actor) throw new IllegalArgumentException("You cannot delete your signed-in account");
            if(m==Module.RESERVATIONS && List.of("CONFIRMED","CHECKED_IN").contains(row.get("status"))) throw new IllegalArgumentException("Cancel or complete this reservation before deletion");
            if(m==Module.GUESTS || m==Module.ROOMS) {
                String column=m==Module.GUESTS?"guest_id":"room_id";
                if(new FileRepository(Module.RESERVATIONS).findAll().stream().anyMatch(x->sameId(x.get(column),id)))
                    throw new DataConflictException("This record is linked to reservations. Remove linked reservations first.");
            }
            if(m==Module.RESERVATIONS && new FileRepository(Module.REVIEWS).findAll().stream().anyMatch(x->sameId(x.get("reservation_id"),id)))
                throw new DataConflictException("This reservation is linked to reviews. Remove linked reviews first.");
            new FileRepository(m).delete(id);
            log(actor+" deleted "+m.table()+" #"+id);
        }
    }
    private static boolean sameId(Object value,long id) {
        try {return FileRepository.id(value)==id;}
        catch(RuntimeException e) {return false;}
    }
    private void log(String message) {try {FileManager.audit(message);} catch(java.io.IOException e) {System.err.println("Audit file unavailable: "+e.getMessage());}}
}
