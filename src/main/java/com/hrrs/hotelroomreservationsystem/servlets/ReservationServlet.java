package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.ReservationFileManager;
/** Owner: Reservation module. GET=list/detail/export, POST=create, PUT=update, DELETE=delete. */
public final class ReservationServlet extends CrudServlet {
    public ReservationServlet() {super(Module.RESERVATIONS,new ReservationFileManager());}
}
