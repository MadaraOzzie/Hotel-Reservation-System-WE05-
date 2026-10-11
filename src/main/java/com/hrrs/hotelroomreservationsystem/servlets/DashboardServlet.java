package com.hrrs.hotelroomreservationsystem.servlets;

import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import com.hrrs.hotelroomreservationsystem.utils.*;
import com.hrrs.hotelroomreservationsystem.utils.Module;
public final class DashboardServlet extends ApiServlet {
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse s) throws IOException {
        try {
            var reservations=service.list(Module.RESERVATIONS);var rooms=service.list(Module.ROOMS);var guests=service.list(Module.GUESTS);
            String today=LocalDate.now().toString();
            long occupied=reservations.stream().filter(b->"CHECKED_IN".equals(b.get("status")) && b.get("check_in").toString().compareTo(today)<=0 && b.get("check_out").toString().compareTo(today)>0).map(b->b.get("room_id")).distinct().count();
            var response=new LinkedHashMap<String,Object>();
            response.put("today",today);response.put("rooms",rooms.size());response.put("guests",guests.size());response.put("occupied",occupied);
            response.put("arrivals",reservations.stream().filter(b->today.equals(b.get("check_in"))&&"CONFIRMED".equals(b.get("status"))).count());
            response.put("departures",reservations.stream().filter(b->today.equals(b.get("check_out"))&&"CHECKED_IN".equals(b.get("status"))).count());
            response.put("reservations",reservations);response.put("roomInventory",rooms);response.put("guestDirectory",guests);
            response.put("activity",FileManager.recentActivity());json(s,response);
        } catch(SQLException e) {throw new RuntimeException(e);}
    }
}
