package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.RoomFileManager;

/**
 * Owner: Room module. GET=list/detail/export, POST=create, PUT=update, DELETE=delete.
 */
public final class RoomServlet extends CrudServlet {
    public RoomServlet() {
        super(Module.ROOMS, new RoomFileManager());
    }
}
