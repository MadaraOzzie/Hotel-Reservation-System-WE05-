package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.FileManager;
import com.hrrs.hotelroomreservationsystem.utils.Module;

public final class StaffServlet extends CrudServlet {
    public StaffServlet() {
        super(Module.STAFF, new FileManager(Module.STAFF));
    }
}
