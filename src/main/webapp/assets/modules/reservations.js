'use strict';
// Member 03: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.reservations = {label:'Reservations',singular:'reservation',description:'Confident bookings, clear dates, and calm arrivals.',filter:'status',columns:[['guest_id','Guest'],['room_id','Room'],['check_in','Check-in'],['check_out','Check-out'],['party_size','Party'],['type','Type'],['status','Status'],['total','Total']],fields:[['guest_id','Guest','guest'],['room_id','Room','room'],['check_in','Check-in date','date'],['check_out','Check-out date','date'],['party_size','Party size','number'],['type','Booking type','select',['ADVANCE','WALK_IN']],['status','Status','select',['CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED']]]};
