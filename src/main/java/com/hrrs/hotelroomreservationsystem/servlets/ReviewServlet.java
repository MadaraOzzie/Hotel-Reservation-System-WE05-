package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.FileManager;
import com.hrrs.hotelroomreservationsystem.utils.Module;

public final class ReviewServlet extends CrudServlet {
    public ReviewServlet() {
        super(Module.REVIEWS, new FileManager(Module.REVIEWS));
    }
}
