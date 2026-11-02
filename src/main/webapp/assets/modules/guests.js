'use strict';
// Member 01: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.guests = {label:'Guests',singular:'guest',description:'Every guest, thoughtfully remembered.',filter:'type',columns:[['name','Guest'],['email','Email'],['phone','Phone'],['type','Membership'],['discount_rate','Discount']],fields:[['name','Full name','text'],['email','Email address','email'],['phone','Phone number','tel'],['type','Membership','select',['STANDARD','VIP']],['discount_rate','Discount rate','number',null,'Use 0.10 for a 10% VIP discount. Standard guests receive no discount.']]};
