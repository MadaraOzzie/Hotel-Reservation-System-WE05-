package com.hrrs.hotelroomreservationsystem.utils;

import java.util.List;
import java.util.Map;

public interface Repository {
    List<Map<String, Object>> findAll();

    Map<String, Object> findById(long id);

    long save(long id, Map<String, Object> fields);

    void delete(long id);
}
