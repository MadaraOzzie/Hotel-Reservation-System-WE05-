package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.GuestFileManager;

/**
 * Owner: Guest module. GET=list/detail/export, POST=create, PUT=update, DELETE=delete.
 */
public final class GuestServlet extends CrudServlet {
    public GuestServlet() {
        super(Module.GUESTS, new GuestFileManager());
    }
}
