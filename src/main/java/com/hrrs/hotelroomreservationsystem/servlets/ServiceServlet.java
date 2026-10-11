package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.ServiceFileManager;
/** Owner: Service module. GET=list/detail/export, POST=create, PUT=update, DELETE=delete. */
public final class ServiceServlet extends CrudServlet {
    public ServiceServlet() {super(Module.SERVICES,new ServiceFileManager());}
}
