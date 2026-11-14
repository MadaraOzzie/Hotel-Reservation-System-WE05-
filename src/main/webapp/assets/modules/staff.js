'use strict';
// Member 04: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.staff = {label:'Team',singular:'team member',description:'The people who make every stay feel considered.',filter:'role',columns:[['name','Name'],['email','Email'],['phone','Phone'],['role','Role'],['shift','Shift']],fields:[['name','Full name','text'],['email','Email address','email'],['phone','Phone number','tel'],['role','Role','select',['MANAGER','RECEPTIONIST']],['shift','Shift','select',['MORNING','EVENING','NIGHT']],['password','Password','password',null,'At least 10 characters. Leave blank when editing to keep the current password.']]};
