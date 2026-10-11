'use strict';
// Member 05: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.services = {label:'Services',singular:'service',description:'Small details and exceptional experiences for every stay.',filter:'category',columns:[['name','Amenity'],['category','Category'],['type','Offering'],['effective_cost','Price'],['status','Status']],fields:[['name','Service name','text'],['category','Category','select',['DINING','WELLNESS','TRANSPORT','LEISURE']],['type','Offering type','select',['PAID','COMPLIMENTARY']],['base_cost','Price (LKR)','number'],['status','Availability','select',['ACTIVE','UNAVAILABLE']],['description','Description','textarea']]};
