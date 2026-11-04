package com.hrrs.hotelroomreservationsystem.utils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Reads and writes module records through UTF-8 JSON files.
 */
public class FileRepository implements Repository {
    private final Module module;

    public FileRepository(Module module) {
        this.module = module;
    }

    @Override
    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> rows = FileStore.read(module);
        rows.sort(Comparator.comparingLong((Map<String, Object> row) -> id(row.get("id"))).reversed());
        return rows;
    }

    @Override
    public Map<String, Object> findById(long id) {
        return findAll().stream().filter(row -> id(row.get("id")) == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Record no longer exists"));
    }

    @Override
    public long save(long id, Map<String, Object> fields) {
        List<String> columns = module.columns().stream().filter(fields::containsKey).toList();
        if (columns.isEmpty())
            throw new IllegalArgumentException("No fields to save");
        return FileStore.update(module, rows -> {
            long target = id;
            Map<String, Object> record;
            if (id == 0) {
                target = rows.stream().mapToLong(row -> id(row.get("id"))).max().orElse(0) + 1;
                record = new LinkedHashMap<>();
                record.put("id", target);
            } else {
                record = rows.stream().filter(row -> id(row.get("id")) == id).findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Record no longer exists"));
            }
            for (String column : columns)
                record.put(column, fields.get(column));
            checkUnique(rows, target, record);
            if (id == 0)
                rows.add(record);
            return target;
        });
    }

    @Override
    public void delete(long id) {
        FileStore.update(module, rows -> {
            if (!rows.removeIf(row -> id(row.get("id")) == id))
                throw new NoSuchElementException("Record no longer exists");
            return null;
        });
    }

    private void checkUnique(List<Map<String, Object>> rows, long id, Map<String, Object> record) {
        String column = switch (module) {
            case GUESTS, SERVICES -> module == Module.GUESTS ? "email" : "name";
            case ROOMS -> "number";
            case STAFF -> "email";
            default -> null;
        };
        if (column == null || !record.containsKey(column))
            return;
        String value = String.valueOf(record.get(column));
        boolean duplicate = rows.stream().filter(row -> id(row.get("id")) != id)
                .anyMatch(row -> value.equalsIgnoreCase(String.valueOf(row.get(column))));
        if (duplicate)
            throw new DataConflictException(switch (module) {
                case GUESTS, STAFF -> "This email address is already in use";
                case ROOMS -> "This room number already exists";
                case SERVICES -> "This service name already exists";
                default -> "Duplicate value";
            });
    }

    static long id(Object value) {
        try {
            return new BigDecimal(String.valueOf(value)).longValue();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid record id: " + value, e);
        }
    }
}
