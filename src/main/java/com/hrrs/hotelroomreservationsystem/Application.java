package com.hrrs.hotelroomreservationsystem;

import com.hrrs.hotelroomreservationsystem.utils.Database;
import com.hrrs.hotelroomreservationsystem.servlets.*;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;

import java.nio.file.*;

public final class Application {
    private Application() {
    }

    public static void main(String[] args) throws Exception {
        Database.initialize();
        Tomcat server = new Tomcat();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8095"));
        server.setPort(port);
        server.setBaseDir("data/tomcat");
        server.getConnector().setProperty("address", System.getenv().getOrDefault("HOTEL_BIND", "127.0.0.1"));
        Context context = server.addWebapp("", Path.of("src/main/webapp").toAbsolutePath().toString());
        context.setParentClassLoader(Application.class.getClassLoader());
        Tomcat.addServlet(context, "auth", new AuthServlet());
        context.addServletMappingDecoded("/api/auth/*", "auth");
        Tomcat.addServlet(context, "dashboard", new DashboardServlet());
        context.addServletMappingDecoded("/api/dashboard", "dashboard");
        Tomcat.addServlet(context, "guests", new GuestServlet());
        context.addServletMappingDecoded("/api/guests/*", "guests");
        Tomcat.addServlet(context, "rooms", new RoomServlet());
        context.addServletMappingDecoded("/api/rooms/*", "rooms");
        Tomcat.addServlet(context, "reservations", new ReservationServlet());
        context.addServletMappingDecoded("/api/reservations/*", "reservations");
        Tomcat.addServlet(context, "staff", new StaffServlet());
        context.addServletMappingDecoded("/api/staff/*", "staff");
        Tomcat.addServlet(context, "services", new ServiceServlet());
        context.addServletMappingDecoded("/api/services/*", "services");
        Tomcat.addServlet(context, "reviews", new ReviewServlet());
        context.addServletMappingDecoded("/api/reviews/*", "reviews");
        server.start();
        if (!server.getConnector().getState().isAvailable()) {
            server.stop();
            throw new IllegalStateException("Unable to bind HTTP port " + port + ". Set PORT to a free port.");
        }
        System.out.println("Serene Hotel is ready: http://localhost:" + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
            } catch (Exception ignored) {
            }
        }));
        server.getServer().await();
    }
}
