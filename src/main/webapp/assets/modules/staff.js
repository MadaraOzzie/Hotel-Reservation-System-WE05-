'use strict';
// Member 04: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.staff = {label:'Team',singular:'team member',description:'The people who make every stay feel personal.',filter:'role',columns:[['name','Team member'],['email','Email'],['phone','Phone'],['role','Role'],['shift','Shift']],fields:[['name','Full name','text'],['email','Work email','email'],['phone','Phone number','tel'],['role','Access role','select',['RECEPTIONIST','MANAGER']],['shift','Shift','select',['MORNING','EVENING','NIGHT']],['password','Password','password',null,'10–128 characters. Leave blank when editing to keep the current password.']]};
