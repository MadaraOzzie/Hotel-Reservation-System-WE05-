'use strict';
// Member 05: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.services = {label:'Services',singular:'service',description:'Thoughtful amenities, ready when your guests need them.',filter:'status',columns:[['name','Service'],['category','Category'],['type','Pricing'],['effective_cost','Effective cost'],['status','Status'],['description','Description']],fields:[['name','Service name','text'],['category','Category','select',['DINING','WELLNESS','TRANSPORT','LEISURE']],['type','Pricing type','select',['PAID','COMPLIMENTARY']],['base_cost','Base cost (LKR)','number'],['status','Status','select',['ACTIVE','UNAVAILABLE']],['description','Description','textarea']]};
