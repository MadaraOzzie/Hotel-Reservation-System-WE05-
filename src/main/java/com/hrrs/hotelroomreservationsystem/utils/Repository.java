package com.hrrs.hotelroomreservationsystem.utils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
public interface Repository {
    List<Map<String,Object>> findAll() throws SQLException;
    Map<String,Object> findById(long id) throws SQLException;
    long save(long id, Map<String,Object> fields) throws SQLException;
    void delete(long id) throws SQLException;
}
