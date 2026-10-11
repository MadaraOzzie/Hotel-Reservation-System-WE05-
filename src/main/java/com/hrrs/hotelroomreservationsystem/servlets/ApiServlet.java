package com.hrrs.hotelroomreservationsystem.servlets;

import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.google.gson.*;
import com.hrrs.hotelroomreservationsystem.utils.*;
import com.hrrs.hotelroomreservationsystem.utils.Module;
public abstract class ApiServlet extends HttpServlet {
    protected static final Gson JSON=new Gson();
    protected final HotelServiceLayer service=new HotelServiceLayer();
    @Override protected void service(HttpServletRequest req,HttpServletResponse res) throws IOException {
        res.setCharacterEncoding("UTF-8");res.setContentType("application/json");
        res.setHeader("Cache-Control","no-store");res.setHeader("X-Content-Type-Options","nosniff");
        try {
            if(!publicAccess(req)) requireUser(req);
            if(!List.of("GET","HEAD","OPTIONS").contains(req.getMethod())) {
                HttpSession s=req.getSession(false);
                if(s==null || !Objects.equals(s.getAttribute("csrf"),req.getHeader("X-CSRF-Token"))) throw new HttpError(403,"Session expired. Refresh and try again.");
            }
            super.service(req,res);
        } catch(HttpError e) {error(res,e.status,e.getMessage());}
        catch(IllegalArgumentException|JsonParseException e) {error(res,400,e.getMessage()==null?"Invalid request":e.getMessage());}
        catch(NoSuchElementException e) {error(res,404,e.getMessage());}
        catch(SQLException e) {
            if(e.getSQLState()!=null && e.getSQLState().startsWith("23")) error(res,409,"This value already exists, or the record is linked to other records. Remove linked records first.");
            else {getServletContext().log("Database error",e);error(res,500,"Database operation failed. Please retry.");}
        } catch(Exception e) {getServletContext().log("Request failed",e);error(res,500,"Unable to complete the request.");}
    }
    protected boolean publicAccess(HttpServletRequest r) {return false;}
    protected Map<String,Object> body(HttpServletRequest r) throws IOException {
        if(r.getContentLengthLong()>16384) throw new HttpError(413,"Request too large");
        StringBuilder text=new StringBuilder(); char[] buffer=new char[1024]; int n;
        try(Reader reader=r.getReader()) {while((n=reader.read(buffer))!=-1) {text.append(buffer,0,n);if(text.length()>16384) throw new HttpError(413,"Request too large");}}
        JsonElement parsed=JsonParser.parseString(text.toString());if(!parsed.isJsonObject()) throw new IllegalArgumentException("Expected an object");
        Map<String,Object> result=new LinkedHashMap<>();
        parsed.getAsJsonObject().entrySet().forEach(e->{if(e.getValue().isJsonNull()) result.put(e.getKey(),null);else if(e.getValue().isJsonPrimitive()) result.put(e.getKey(),e.getValue().getAsString());else throw new IllegalArgumentException("Invalid field");});
        return result;
    }
    protected void json(HttpServletResponse r,Object value) throws IOException {r.getWriter().write(JSON.toJson(value));}
    protected void error(HttpServletResponse r,int status,String message) throws IOException {r.setStatus(status);json(r,Map.of("error",message));}
    protected long requireUser(HttpServletRequest r) throws SQLException {
        HttpSession s=r.getSession(false);
        if(s==null || s.getAttribute("userId")==null) throw new HttpError(401,"Please sign in");
        long id=(Long)s.getAttribute("userId");
        try(Connection c=Database.open()) {
            try {Map<String,Object> row=new SqlRepository(c,Module.STAFF).findById(id);s.setAttribute("role",row.get("role"));s.setAttribute("name",row.get("name"));}
            catch(NoSuchElementException e) {s.invalidate();throw new HttpError(401,"Account no longer exists");}
        }
        return id;
    }
    protected void manager(HttpServletRequest r) {if(!"MANAGER".equals(r.getSession().getAttribute("role"))) throw new HttpError(403,"Manager access required");}
    protected static final class HttpError extends RuntimeException {
        final int status; HttpError(int status,String message) {super(message);this.status=status;}
    }
}
