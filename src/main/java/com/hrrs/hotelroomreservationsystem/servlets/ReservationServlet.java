package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.FileManager;
import com.hrrs.hotelroomreservationsystem.utils.Module;

public final class ReservationServlet extends CrudServlet {
    public ReservationServlet() {
        super(Module.RESERVATIONS, new FileManager(Module.RESERVATIONS));
    }
}
