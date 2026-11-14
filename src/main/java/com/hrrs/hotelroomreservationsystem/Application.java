package com.hrrs.hotelroomreservationsystem;

import com.hrrs.hotelroomreservationsystem.utils.FileStore;
import com.hrrs.hotelroomreservationsystem.servlets.*;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.*;
public final class Application {
    private Application() {}
    public static void main(String[] args) throws Exception {
        FileStore.initialize();
        int port=Integer.parseInt(System.getenv().getOrDefault("PORT","8095"));
        String bind=System.getenv().getOrDefault("HOTEL_BIND","127.0.0.1");
        if (!isPortFree(bind,port)) {
            if (isAlreadyRunning(port)) {
                System.out.println("Serene Hotel is already running: http://localhost:"+port);
                return;
            }
            System.err.println("Port "+port+" is already in use by another program.");
            System.err.println("Stop that program, or start on a free port. Example: set PORT=8096");
            System.exit(1);
        }
        Tomcat server=new Tomcat();
        server.setPort(port); server.setBaseDir("data/tomcat");
        server.getConnector().setProperty("address",bind);
        Context context=server.addWebapp("",Path.of("src/main/webapp").toAbsolutePath().toString());
        context.setParentClassLoader(Application.class.getClassLoader());
        Tomcat.addServlet(context,"auth",new AuthServlet()); context.addServletMappingDecoded("/api/auth/*","auth");
        Tomcat.addServlet(context,"dashboard",new DashboardServlet()); context.addServletMappingDecoded("/api/dashboard","dashboard");
        Tomcat.addServlet(context,"guests",new GuestServlet());context.addServletMappingDecoded("/api/guests/*","guests");
        Tomcat.addServlet(context,"rooms",new RoomServlet());context.addServletMappingDecoded("/api/rooms/*","rooms");
        Tomcat.addServlet(context,"reservations",new ReservationServlet());context.addServletMappingDecoded("/api/reservations/*","reservations");
        Tomcat.addServlet(context,"staff",new StaffServlet());context.addServletMappingDecoded("/api/staff/*","staff");
        Tomcat.addServlet(context,"services",new ServiceServlet());context.addServletMappingDecoded("/api/services/*","services");
        Tomcat.addServlet(context,"reviews",new ReviewServlet());context.addServletMappingDecoded("/api/reviews/*","reviews");
        server.start();
        System.out.println("Serene Hotel is ready: http://localhost:"+port);
        Runtime.getRuntime().addShutdownHook(new Thread(()->{try {server.stop();} catch(Exception ignored) {}}));
        server.getServer().await();
    }
    private static boolean isPortFree(String host,int port) {
        try (ServerSocket socket=new ServerSocket()) {
            socket.setReuseAddress(false);
            socket.bind(new InetSocketAddress(host,port));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private static boolean isAlreadyRunning(int port) {
        HttpURLConnection c=null;
        try {
            c=(HttpURLConnection)new URL("http://127.0.0.1:"+port+"/").openConnection();
            c.setConnectTimeout(2000); c.setReadTimeout(2000); c.setRequestMethod("GET");
            return c.getResponseCode()==200;
        } catch (Exception e) {
            return false;
        } finally {
            if (c!=null) c.disconnect();
        }
    }
}
