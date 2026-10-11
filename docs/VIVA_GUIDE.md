# Viva and demonstration guide

## Ten-minute team demonstration

1. **Member 4:** launch the application, sign in as manager and explain session/role/CSRF checks. Create a temporary receptionist account, edit its shift and password, then delete it after demonstrating login in another browser context.
2. **Member 1:** create a VIP guest with 0.10 discount, search for them and update contact details. Explain User → Guest → VIPGuest and how private state is accessed through methods.
3. **Member 2:** create a suite with capacity 4, base rate LKR 10,000 and tax 0.10. Explain why the nightly rate is LKR 11,000 and how the override is invoked through Room.
4. **Member 3:** create a two-night advance booking for that VIP guest/suite. Expected total: LKR 19,800. Attempt an overlapping booking and show rejection; create an adjacent booking to explain half-open date ranges. Edit dates; cancel and show the 10% cancellation fee. Delete cancelled test bookings.
5. **Member 5:** create a paid amenity, edit it to complimentary, show zero effective cost, search and delete. Explain the abstract HotelService contract and override.
6. **Member 6:** create an anonymous review and show the anonymous display tag. Create a verified review linked to a completed stay; show validation rejection for an active booking. Edit/hide then delete a temporary review.
7. **All:** export a module `.txt`, inspect it, show sample files and activity.txt; show class diagrams, actual Git history and passing tests. Remove temporary guest and room after deleting their reservations.

## Questions each person should be able to answer

- What does your constructor protect? Which fields are private and why?
- Which parent method do you override? Where does the application invoke it through a parent type?
- What is the difference between an abstract class and Repository's interface?
- How does your servlet distinguish list/create/update/delete?
- Why does an invalid request fail on the server even if browser validation is bypassed?
- How are prepared-statement parameters different from concatenating SQL values?
- What does commit/rollback mean? What would happen if a guest deletion cascaded into stays?
- Why are SQL export snapshots not sufficient for password-preserving database restoration?
- What happens to room availability when a booking is cancelled?
- Why is money BigDecimal rather than double?
- Why must date-specific booking checks and insertion share one lock/transaction?
- Why is the UI hidden button not an authorization mechanism?
- Where does Java read a text file? Where does it write one? How are resources closed?

## Trace one example

Reservation create form → `POST /api/reservations` → ApiServlet requires a valid session and CSRF token → ReservationServlet inherits CrudServlet.doPost → HotelServiceLayer.save builds allowed fields → validate checks guest/room/dates/capacity/overlap → DomainFactory builds AdvanceBooking, SuiteRoom and VIPGuest → Reservation.calculateTotal dispatches overridden pricing/discount methods → SqlRepository inserts with prepared parameters → transaction commits → audit line is appended → UI returns to the reservation list.

Do not memorize code without understanding it. Each member should run and modify their module, explain a meaningful test and show genuine commit evidence.
