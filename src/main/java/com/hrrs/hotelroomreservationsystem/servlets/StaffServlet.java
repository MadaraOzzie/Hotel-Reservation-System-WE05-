package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.StaffFileManager;
/** Owner: Staff module. GET=list/detail/export, POST=create, PUT=update, DELETE=delete. */
public final class StaffServlet extends CrudServlet {
    public StaffServlet() {super(Module.STAFF,new StaffFileManager());}
}
