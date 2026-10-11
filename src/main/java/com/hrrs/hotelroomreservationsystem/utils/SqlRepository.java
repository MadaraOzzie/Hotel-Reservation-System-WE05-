package com.hrrs.hotelroomreservationsystem.utils;

import java.sql.*;
import java.util.*;
/** Connection belongs to the service transaction; statements/results belong to this DAO. */
public class SqlRepository implements Repository {
    private final Connection connection;
    private final Module module;
    public SqlRepository(Connection connection, Module module) { this.connection=connection; this.module=module; }
    private Map<String,Object> row(ResultSet rs) throws SQLException {
        Map<String,Object> row = new LinkedHashMap<>();
        for (int i=1;i<=rs.getMetaData().getColumnCount();i++) {
            Object value=rs.getObject(i);
            if (value instanceof java.sql.Date || value instanceof java.sql.Timestamp) value=value.toString();
            row.put(rs.getMetaData().getColumnLabel(i).toLowerCase(Locale.ROOT),value);
        }
        return row;
    }
    @Override public List<Map<String,Object>> findAll() throws SQLException {
        List<Map<String,Object>> list=new ArrayList<>();
        try (PreparedStatement st=connection.prepareStatement("SELECT * FROM "+module.table()+" ORDER BY id DESC"); ResultSet rs=st.executeQuery()) {
            while (rs.next()) list.add(row(rs));
        }
        return list;
    }
    @Override public Map<String,Object> findById(long id) throws SQLException {
        try (PreparedStatement st=connection.prepareStatement("SELECT * FROM "+module.table()+" WHERE id=?")) {
            st.setLong(1,id); try (ResultSet rs=st.executeQuery()) { if(rs.next()) return row(rs); }
        }
        throw new NoSuchElementException("Record no longer exists");
    }
    @Override public long save(long id, Map<String,Object> fields) throws SQLException {
        List<String> columns=module.columns().stream().filter(fields::containsKey).toList();
        if(columns.isEmpty()) throw new IllegalArgumentException("No fields to save");
        String sql=id==0 ? "INSERT INTO "+module.table()+" ("+String.join(",",columns)+") VALUES ("+String.join(",",Collections.nCopies(columns.size(),"?"))+")"
            : "UPDATE "+module.table()+" SET "+String.join(",",columns.stream().map(k->k+"=?").toList())+" WHERE id=?";
        try (PreparedStatement st=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            int i=1; for(String key:columns) st.setObject(i++,fields.get(key)); if(id!=0) st.setLong(i,id);
            if(st.executeUpdate()!=1) throw new NoSuchElementException("Record no longer exists");
            if(id!=0) return id;
            try(ResultSet keys=st.getGeneratedKeys()) { keys.next(); return keys.getLong(1); }
        }
    }
    @Override public void delete(long id) throws SQLException {
        try(PreparedStatement st=connection.prepareStatement("DELETE FROM "+module.table()+" WHERE id=?")) {
            st.setLong(1,id); if(st.executeUpdate()!=1) throw new NoSuchElementException("Record no longer exists");
        }
    }
}
