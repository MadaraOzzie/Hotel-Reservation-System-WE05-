package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.FileManager;
import com.hrrs.hotelroomreservationsystem.utils.Module;

public final class ServiceServlet extends CrudServlet {
    public ServiceServlet() {
        super(Module.SERVICES, new FileManager(Module.SERVICES));
    }
}
