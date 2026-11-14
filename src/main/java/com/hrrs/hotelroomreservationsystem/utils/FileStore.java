package com.hrrs.hotelroomreservationsystem.utils;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** UTF-8 JSON files are the canonical application store. */
public final class FileStore {
    private static final Object LOCK=new Object();
    private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();
    private static Path root=Path.of("data","store");
    private FileStore() {}

    public static void useDirectory(Path directory) {
        synchronized(LOCK) { root=directory.toAbsolutePath().normalize(); }
    }
    public static Path directory() {
        synchronized(LOCK) { return root; }
    }
    /** Reads all records for a module. Missing files are treated as empty. */
    public static List<Map<String,Object>> read(Module module) {
        synchronized(LOCK) { return new ArrayList<>(readUnlocked(module)); }
    }
    /** Runs a read-modify-write cycle under one lock and persists atomically. */
    public static <T> T update(Module module,Change<T> change) {
        synchronized(LOCK) {
            List<Map<String,Object>> rows=readUnlocked(module);
            T result=change.apply(rows);
            writeUnlocked(module,rows);
            return result;
        }
    }
    public static void initialize() throws IOException {
        synchronized(LOCK) {
            Files.createDirectories(root);
            Path marker=root.resolve(".seeded");
            if(Files.exists(marker)) return;
            seed();
            Files.writeString(marker,"1",StandardCharsets.UTF_8);
        }
    }
    public interface Change<T> { T apply(List<Map<String,Object>> rows); }

    private static List<Map<String,Object>> readUnlocked(Module module) {
        Path path=root.resolve(module.table()+".json");
        if(!Files.exists(path)) return new ArrayList<>();
        try(Reader reader=Files.newBufferedReader(path,StandardCharsets.UTF_8)) {
            JsonElement parsed=JsonParser.parseReader(reader);
            if(parsed==null || parsed.isJsonNull()) return new ArrayList<>();
            if(!parsed.isJsonArray()) throw new IllegalStateException("Invalid data file: "+path);
            List<Map<String,Object>> rows=new ArrayList<>();
            for(JsonElement element:parsed.getAsJsonArray()) {
                if(!element.isJsonObject()) throw new IllegalStateException("Invalid data file: "+path);
                Map<String,Object> row=new LinkedHashMap<>();
                for(Map.Entry<String,JsonElement> field:element.getAsJsonObject().entrySet()) row.put(field.getKey(),value(field.getValue()));
                rows.add(row);
            }
            return rows;
        } catch(IOException e) {throw new UncheckedIOException("Unable to read "+path,e);}
        catch(JsonParseException e) {throw new IllegalStateException("Invalid data file: "+path,e);}
    }
    private static Object value(JsonElement element) {
        if(element==null || element.isJsonNull()) return null;
        if(!element.isJsonPrimitive()) throw new IllegalStateException("Nested values are not supported");
        JsonPrimitive primitive=element.getAsJsonPrimitive();
        if(primitive.isNumber()) return primitive.getAsBigDecimal();
        if(primitive.isBoolean()) return primitive.getAsBoolean();
        return primitive.getAsString();
    }
    private static void writeUnlocked(Module module,List<Map<String,Object>> rows) {
        try {
            Files.createDirectories(root);
            Path target=root.resolve(module.table()+".json");
            Path temporary=Files.createTempFile(root,module.table(),".tmp");
            try {
                Files.writeString(temporary,GSON.toJson(rows),StandardCharsets.UTF_8);
                try {Files.move(temporary,target,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
                catch(AtomicMoveNotSupportedException e) {Files.move(temporary,target,StandardCopyOption.REPLACE_EXISTING);}
            } finally {Files.deleteIfExists(temporary);}
        } catch(IOException e) {throw new UncheckedIOException("Unable to write "+module.table(),e);}
    }

    private static void seed() throws IOException {
        for(Module module:Module.values()) seedFromResource(module);
        if(new FileRepository(Module.GUESTS).findAll().isEmpty()) {
            insert(Module.GUESTS,Map.of(
                    "name","Amara Patel","email","amara@example.com","phone","0771234567","type","VIP","discount_rate","0.10"));
            insert(Module.GUESTS,Map.of(
                    "name","Ruben Haris","email","ruben@example.com","phone","0771234568","type","STANDARD","discount_rate","0.00"));
        }
        if(new FileRepository(Module.ROOMS).findAll().isEmpty()) {
            for(Map<String,Object> row:resourceRecords("/rooms.txt")) insert(Module.ROOMS,row);
        }
        if(new FileRepository(Module.STAFF).findAll().isEmpty()) {
            insert(Module.STAFF,Map.of(
                    "name","Tariq Manager","email","manager@serene.local","phone","0771002001","role","MANAGER","shift","MORNING","password_hash",Passwords.hash("Serene@2026")));
            insert(Module.STAFF,Map.of(
                    "name","Leah Desk","email","desk@serene.local","phone","0771002002","role","RECEPTIONIST","shift","EVENING","password_hash",Passwords.hash("Welcome@2026")));
        }
        if(new FileRepository(Module.SERVICES).findAll().isEmpty()) {
            insert(Module.SERVICES,Map.of(
                    "name","Spa Ritual","category","WELLNESS","type","PAID","base_cost","2500","status","ACTIVE","description","Relaxing wellness treatment"));
            insert(Module.SERVICES,Map.of(
                    "name","Airport Transfer","category","TRANSPORT","type","PAID","base_cost","3500","status","ACTIVE","description","Private airport transfer service"));
            insert(Module.SERVICES,Map.of(
                    "name","Breakfast","category","DINING","type","COMPLIMENTARY","base_cost","0","status","ACTIVE","description","Complimentary breakfast included"));
        }
        if(new FileRepository(Module.RESERVATIONS).findAll().isEmpty()) {
            insert(Module.RESERVATIONS,Map.of(
                    "guest_id","1","room_id","4","check_in","2026-10-11","check_out","2026-10-14","party_size","2","type","ADVANCE","status","CONFIRMED","total","124740.00","cancellation_fee","0.00"));
        }
    }
    private static void seedFromResource(Module module) throws IOException {
        for(Map<String,Object> row:resourceRecords("/sample-data/"+module.table()+".txt")) {
            if(module==Module.STAFF) row.put("password_hash",Passwords.hash((String)row.remove("password")));
            insert(module,row);
        }
    }
    private static List<Map<String,Object>> resourceRecords(String resource) throws IOException {
        try(InputStream input=FileStore.class.getResourceAsStream(resource)) {
            if(input==null) return List.of();
            JsonArray records=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonArray();
            List<Map<String,Object>> rows=new ArrayList<>();
            for(JsonElement element:records) {
                Map<String,Object> row=new LinkedHashMap<>();
                for(Map.Entry<String,JsonElement> field:element.getAsJsonObject().entrySet())
                    row.put(field.getKey(),field.getValue().isJsonNull()?null:field.getValue().getAsString());
                rows.add(row);
            }
            return rows;
        } catch(JsonParseException e) {throw new IllegalStateException("Invalid seed file: "+resource,e);}
    }
    private static void insert(Module module,Map<String,Object> row) {
        new FileRepository(module).save(0,row);
    }
}
