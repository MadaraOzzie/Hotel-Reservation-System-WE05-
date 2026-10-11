# Six-person workload and ownership

Replace Member 1â€“6 with real names/student IDs. The same three packages are used for all members: `models`, `utils`, `servlets`, beneath `com.hrrs.hotelroomreservationsystem`. UI files live in `src/main/webapp`. Shared mechanics avoid six copies of connection/auth/rendering code.

| Member | Module | Model ownership | File/controller ownership | Three UI screens | CRUD responsibility |
|---|---|---|---|---|---|
| 1 | Guests | User, Guest, VIPGuest | GuestFileManager, GuestServlet | Guest list, registration, profile edit | Create profiles, search/read, update membership/contact, delete unlinked guest |
| 2 | Rooms | Room, StandardRoom, SuiteRoom | RoomFileManager, RoomServlet | Room list, add room, edit inventory | Add/search/edit/delete rooms; maintenance and capacity rules |
| 3 | Reservations | Reservation, AdvanceBooking, WalkInBooking | ReservationFileManager, ReservationServlet | Booking list, create booking, edit/cancel/check-in/out | Book/read/change/delete; overlap and price calculations |
| 4 | Team | Staff, Manager, Receptionist | StaffFileManager, StaffServlet, AuthServlet, Passwords | Staff directory, staff creation, staff edit; login | Account CRUD, role checks, password hashing and self-protection |
| 5 | Services | HotelService, PaidService, ComplimentaryService | ServiceFileManager, ServiceServlet | Service list, add amenity, edit amenity | Paid/included service CRUD, effective pricing |
| 6 | Reviews | Review, VerifiedStayReview, AnonymousReview | ReviewFileManager, ReviewServlet | Review list, submit review, edit/moderate review | Review CRUD, rating validation, completed-stay verification |

## Shared files and integration ownership

- Member 1 owns the shared Entity/User contract; changes require coordination with Member 4.
- Member 2 coordinates `schema.sql`, `Database`, `SqlRepository` and the `Repository` interface.
- Member 3 coordinates `HotelServiceLayer` and `DomainFactory`. Each member owns their module's validation branch and factory methods; coordinate edits rather than overwriting one another.
- Member 4 coordinates `ApiServlet`/`CrudServlet`, sessions, authorization and `Application` wiring.
- Member 5 coordinates `assets/styles.css`, `assets/app.js` shell and dashboard; each member owns their separate `assets/modules/guests.js`, `rooms.js`, `reservations.js`, `staff.js`, `services.js` or `reviews.js` file, which defines their editor fields and table columns.
- Member 6 coordinates documentation, file round-trip tests and review rules. Each member contributes module test cases and screenshots.

The UI uses a shared renderer. Three screens per module are separate navigable routes, not three duplicate HTML files. If the lecturer requires separate JSP files per screen, confirm that interpretation before submission; the present project has 18 functional screens rendered from one JSP entry point.

## OOP demonstrations by person

1. Pass a `VIPGuest` as `Guest` into `Reservation.calculateTotal`; overridden discount changes the result.
2. Call `Room.calculateFinalPrice` on StandardRoom and SuiteRoom references; suite tax changes the result.
3. Call `calculateCancellationFee` through Reservation on AdvanceBooking and WalkInBooking.
4. Call `Staff.getAccessLevel` without knowing the subclass; AuthServlet uses it for session roles.
5. Call HotelService.calculateCost for PaidService and ComplimentaryService; the latter always returns zero.
6. Call Review.getDisplayTag for verified/anonymous review objects; output is included in the list API.

All models encapsulate fields, use constructors/getters, inherit shared identity and override domain behavior. Repository is an interface and SqlRepository is its implementation. SQL credentials, hashing and file I/O are hidden behind utility classes.

## Genuine collaboration plan

1. Create a GitHub repository and commit the reviewed initial project, crediting supplied/generated scaffolding as required by your course policy.
2. Assign branches `feature/guests`, `feature/rooms`, `feature/reservations`, `feature/staff`, `feature/services`, `feature/reviews`.
3. Each member reads and explains their classes, implements meaningful refinements, adds tests and records screenshots.
4. Open pull requests. A different member reviews each change. Resolve shared-file conflicts together.
5. Run `mvn test`, then demonstrate module CRUD after integration.
6. Export actual history using `git log --all --date=short --pretty=format:"%h | %ad | %an | %s"` and add it to the report. Do not fabricate commits, backdate contributions, or claim work performed by others.
