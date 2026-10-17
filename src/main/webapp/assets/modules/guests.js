'use strict';
// Member 01: list columns, create/edit form fields, labels and filtering.
window.HotelModules = window.HotelModules || {};
window.HotelModules.guests = {
    label: 'Guests',
    singular: 'guest',
    description: 'Thoughtful hospitality starts with knowing your guests.',
    filter: 'type',
    columns: [['name', 'Guest'], ['email', 'Email'], ['phone', 'Phone'], ['type', 'Membership'], ['discount_rate', 'Discount']],
    fields: [['name', 'Full name', 'text'], ['email', 'Email address', 'email'], ['phone', 'Phone number', 'tel'], ['type', 'Membership', 'select', ['STANDARD', 'VIP']], ['discount_rate', 'VIP discount rate', 'number', null, 'Use 0.10 for 10%. Standard guests receive no discount.']]
};
