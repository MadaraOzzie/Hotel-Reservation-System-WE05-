package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.ReviewFileManager;
/** Owner: Review module. GET=list/detail/export, POST=create, PUT=update, DELETE=delete. */
public final class ReviewServlet extends CrudServlet {
    public ReviewServlet() {super(Module.REVIEWS,new ReviewFileManager());}
}
