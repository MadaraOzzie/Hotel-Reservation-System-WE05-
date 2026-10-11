package com.hrrs.hotelroomreservationsystem.servlets;

import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.hrrs.hotelroomreservationsystem.utils.*;
import com.hrrs.hotelroomreservationsystem.models.Staff;
public final class AuthServlet extends ApiServlet {
    private final Map<String,long[]> failures=new java.util.concurrent.ConcurrentHashMap<>();
    @Override protected boolean publicAccess(HttpServletRequest r) {return true;}
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse s) throws IOException {
        if(!"/session".equals(r.getPathInfo())) {error(s,404,"Unknown endpoint");return;}
        HttpSession session=r.getSession();session.setMaxInactiveInterval(1800);
        if(session.getAttribute("csrf")==null) session.setAttribute("csrf",UUID.randomUUID().toString());
        Map<String,Object> response=new LinkedHashMap<>();response.put("csrf",session.getAttribute("csrf"));
        if(session.getAttribute("userId")!=null) {
            try {requireUser(r);response.put("user",Map.of("id",session.getAttribute("userId"),"name",session.getAttribute("name"),"role",session.getAttribute("role")));}
            catch(SQLException e) {throw new RuntimeException(e);}
        }
        json(s,response);
    }
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse s) throws IOException {
        if("/logout".equals(r.getPathInfo())) {r.getSession().invalidate();json(s,Map.of("ok",true));return;}
        if(!"/login".equals(r.getPathInfo())) {error(s,404,"Unknown endpoint");return;}
        String key=r.getRemoteAddr();long now=System.currentTimeMillis();long[] state=failures.get(key);
        if(state!=null && state[0]>=5 && now-state[1]<60000) throw new HttpError(429,"Too many attempts. Try again in one minute.");
        var input=body(r);String email=Objects.toString(input.get("email"),"").trim().toLowerCase(Locale.ROOT),password=Objects.toString(input.get("password"),"");
        try(Connection c=Database.open();PreparedStatement st=c.prepareStatement("SELECT id,password_hash FROM staff WHERE email=?")) {
            st.setString(1,email);
            try(ResultSet rs=st.executeQuery()) {
                if(rs.next() && Passwords.verify(password,rs.getString("password_hash"))) {
                    long id=rs.getLong("id");
                    Staff staff=DomainFactory.staff(new SqlRepository(c,com.hrrs.hotelroomreservationsystem.utils.Module.STAFF).findById(id));
                    r.changeSessionId();HttpSession session=r.getSession();session.setAttribute("userId",id);session.setAttribute("role",staff.getAccessLevel());session.setAttribute("name",staff.getName());
                    session.setAttribute("csrf",UUID.randomUUID().toString());failures.remove(key);
                    json(s,Map.of("csrf",session.getAttribute("csrf"),"user",Map.of("id",id,"name",staff.getName(),"role",staff.getAccessLevel())));return;
                }
            }
            failures.compute(key,(k,v)->v==null||now-v[1]>=60000?new long[]{1,now}:new long[]{v[0]+1,v[1]});
            throw new HttpError(401,"Email or password is incorrect");
        } catch(SQLException e) {throw new RuntimeException(e);}
    }
}
