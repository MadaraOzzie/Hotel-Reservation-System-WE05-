'use strict';
// Member 06: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.reviews = {label:'Reviews',singular:'review',description:'Listen closely. Make the next stay even better.',filter:'status',columns:[['author','Guest'],['rating','Rating'],['text','Feedback'],['type','Review type'],['reservation_id','Booking'],['status','Visibility']],fields:[['author','Display name','text'],['type','Review type','select',['ANONYMOUS','VERIFIED']],['reservation_id','Completed reservation','booking'],['rating','Rating','select',['5','4','3','2','1']],['status','Visibility','select',['PUBLISHED','HIDDEN']],['text','Guest feedback','textarea']]};
