package com.hrrs.hotelroomreservationsystem.utils;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.time.Instant;
import java.util.*;
import com.google.gson.*;
/** UTF-8 file read/write exercise. SQL remains the canonical application store. */
public class FileManager {
    private final Module module;
    public FileManager(Module module) { this.module=module; }
    public String serialize(List<Map<String,Object>> records) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(records);
    }
    public Path writeSnapshot(List<Map<String,Object>> records) throws IOException {
        Path directory=Path.of("data","exports"); Files.createDirectories(directory);
        Path temporary=Files.createTempFile(directory,module.table(),".tmp");
        try {
            Files.writeString(temporary,serialize(records),StandardCharsets.UTF_8);
            Path destination=directory.resolve(module.table()+".txt");
            Files.move(temporary,destination,StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } finally { Files.deleteIfExists(temporary); }
    }
    public JsonArray readSnapshot(Path path) throws IOException {
        try(Reader reader=Files.newBufferedReader(path,StandardCharsets.UTF_8)) { return JsonParser.parseReader(reader).getAsJsonArray(); }
    }
    public static synchronized void audit(String message) throws IOException {
        Files.createDirectories(Path.of("data"));
        Files.writeString(Path.of("data","activity.txt"),Instant.now()+" "+message.replaceAll("[\r\n]"," ")+System.lineSeparator(),StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    }
    public static List<String> recentActivity() throws IOException {
        Path path=Path.of("data","activity.txt"); if(!Files.exists(path)) return List.of();
        // Stream the file while retaining only a bounded tail.
        ArrayDeque<String> tail=new ArrayDeque<>();
        try(var lines=Files.lines(path,StandardCharsets.UTF_8)) { lines.forEach(line->{ if(tail.size()==8) tail.removeFirst(); tail.addLast(line); }); }
        List<String> result=new ArrayList<>(tail); Collections.reverse(result); return result;
    }
}
