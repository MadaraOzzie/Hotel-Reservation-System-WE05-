'use strict';
// Member 02: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.rooms = {label:'Rooms',singular:'room',description:'A clear view of every room, rate, and availability.',filter:'status',columns:[['number','Room'],['type','Room type'],['capacity','Capacity'],['nightly_price','Nightly rate'],['status','Inventory status']],fields:[['number','Room number','text'],['type','Room type','select',['STANDARD','SUITE']],['capacity','Guest capacity','number'],['base_price','Base rate (LKR / night)','number'],['luxury_tax','Suite tax rate','number',null,'Use 0.10 for 10%. Standard rooms have no suite tax.'],['status','Inventory status','select',['AVAILABLE','MAINTENANCE']]]};
