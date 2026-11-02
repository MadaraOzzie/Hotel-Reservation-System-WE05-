package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.FileManager;
import com.hrrs.hotelroomreservationsystem.utils.Module;

public final class GuestServlet extends CrudServlet {
    public GuestServlet() {
        super(Module.GUESTS, new FileManager(Module.GUESTS));
    }
}
