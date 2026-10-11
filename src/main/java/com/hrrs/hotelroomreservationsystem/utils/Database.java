package com.hrrs.hotelroomreservationsystem.utils;

import java.nio.file.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;
public final class Database {
    private static String url=System.getenv().getOrDefault("HOTEL_DB_URL","jdbc:h2:file:./data/hotel;DB_CLOSE_ON_EXIT=FALSE");
    private Database() {}
    public static void useUrl(String value) { url=value; }
    public static Connection open() throws SQLException {
        return DriverManager.getConnection(url,System.getenv().getOrDefault("HOTEL_DB_USER","sa"),System.getenv().getOrDefault("HOTEL_DB_PASSWORD",""));
    }
    public static void initialize() throws Exception {
        Files.createDirectories(Path.of("data"));
        try(Connection c=open(); InputStream in=Database.class.getResourceAsStream("/schema.sql")) {
            if(in==null) throw new FileNotFoundException("schema.sql missing");
            String schema=new String(in.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);
            for(String sql:schema.split(";")) if(!sql.isBlank()) try(Statement st=c.createStatement()) { st.execute(sql); }
            try(Statement st=c.createStatement()) { st.execute("CREATE TABLE IF NOT EXISTS app_metadata (meta_key VARCHAR(60) PRIMARY KEY, meta_value VARCHAR(100))"); }
            try(PreparedStatement st=c.prepareStatement("SELECT meta_value FROM app_metadata WHERE meta_key='seeded'"); ResultSet rs=st.executeQuery()) { if(rs.next()) return; }
            c.setAutoCommit(false);
            try {
                for(Module module:List.of(Module.GUESTS,Module.ROOMS,Module.STAFF,Module.SERVICES,Module.RESERVATIONS,Module.REVIEWS)) {
                    try(InputStream input=Database.class.getResourceAsStream("/sample-data/"+module.table()+".txt")) {
                        if(input==null) throw new FileNotFoundException("Missing sample data");
                        JsonArray records=JsonParser.parseReader(new InputStreamReader(input,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonArray();
                        for(JsonElement element:records) {
                            Map<String,Object> row=new LinkedHashMap<>();
                            element.getAsJsonObject().entrySet().forEach(e->row.put(e.getKey(),e.getValue().isJsonNull()?null:e.getValue().getAsString()));
                            if(module==Module.STAFF) row.put("password_hash",Passwords.hash((String)row.remove("password")));
                            new SqlRepository(c,module).save(0,row);
                        }
                    }
                }
                try(Statement st=c.createStatement()) { st.executeUpdate("INSERT INTO app_metadata VALUES ('seeded','1')"); }
                c.commit();
            } catch(Exception e) { c.rollback(); throw e; }
        }
    }
}
