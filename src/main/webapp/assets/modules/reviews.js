'use strict';
// Member 06: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.reviews = {label:'Reviews',singular:'review',description:'Guest feedback, kept honest and thoughtfully moderated.',filter:'status',columns:[['reservation_id','Stay'],['author','Author'],['type','Type'],['rating','Rating'],['text','Review'],['status','Status']],fields:[['type','Review type','select',['VERIFIED','ANONYMOUS']],['reservation_id','Completed stay','booking'],['author','Author name','text',null,'Anonymous reviews use the author name "Anonymous".'],['rating','Rating','number',null,'Choose a rating from 1 to 5.'],['text','Review text','textarea'],['status','Status','select',['PUBLISHED','HIDDEN']]]};
