package com.hrrs.hotelroomreservationsystem.servlets;

import jakarta.servlet.http.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import com.hrrs.hotelroomreservationsystem.utils.*;
import com.hrrs.hotelroomreservationsystem.utils.Module;
/** Six concrete controllers reuse HTTP mechanics; domain rules stay in the service. */
public abstract class CrudServlet extends ApiServlet {
    private final Module module;
    private final FileManager files;
    protected CrudServlet(Module module, FileManager files) {this.module=module;this.files=files;}
    private long id(HttpServletRequest r) {
        try {long id=Long.parseLong(r.getPathInfo().substring(1));if(id<1) throw new NumberFormatException();return id;}
        catch(Exception e) {throw new IllegalArgumentException("Invalid record ID");}
    }
    private void permission(HttpServletRequest r,boolean write) {
        if(module==Module.STAFF || (write && List.of(Module.ROOMS,Module.SERVICES,Module.REVIEWS).contains(module))) manager(r);
    }
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse s) throws IOException {
        permission(r,false);
        try {
            var rows=service.list(module);
            if("/export".equals(r.getPathInfo())) {
                manager(r);var path=files.writeSnapshot(rows);
                // Read back the generated UTF-8 file, demonstrating both file operations.
                var parsed=files.readSnapshot(path);
                s.setContentType("text/plain; charset=UTF-8");s.setHeader("Content-Disposition","attachment; filename="+module.table()+".txt");
                s.getWriter().write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(parsed));
            } else if(r.getPathInfo()!=null && !r.getPathInfo().equals("/")) {
                long id=id(r);json(s,rows.stream().filter(row->((Number)row.get("id")).longValue()==id).findFirst().orElseThrow(()->new NoSuchElementException("Record not found")));
            } else json(s,rows);
        } catch(SQLException e) {throw new RuntimeException(e);}
    }
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse s) throws IOException {
        permission(r,true);
        if(r.getPathInfo()!=null && !r.getPathInfo().equals("/")) throw new IllegalArgumentException("Use the collection URL to create a record");
        try {long id=service.save(module,0,body(r),(Long)r.getSession().getAttribute("userId"));s.setStatus(201);json(s,Map.of("id",id));}
        catch(SQLException e) {sqlError(s,e);}
    }
    @Override protected void doPut(HttpServletRequest r,HttpServletResponse s) throws IOException {
        permission(r,true);
        try {long id=service.save(module,id(r),body(r),(Long)r.getSession().getAttribute("userId"));json(s,Map.of("id",id));}
        catch(SQLException e) {sqlError(s,e);}
    }
    @Override protected void doDelete(HttpServletRequest r,HttpServletResponse s) throws IOException {
        permission(r,true);
        try {service.delete(module,id(r),(Long)r.getSession().getAttribute("userId"));json(s,Map.of("deleted",true));}
        catch(SQLException e) {sqlError(s,e);}
    }
    private void sqlError(HttpServletResponse s,SQLException e) throws IOException {
        if(e.getSQLState()!=null && e.getSQLState().startsWith("23")) error(s,409,"Duplicate value or linked record. Remove linked records first.");
        else {getServletContext().log("SQL failure",e);error(s,500,"Database operation failed");}
    }
}
