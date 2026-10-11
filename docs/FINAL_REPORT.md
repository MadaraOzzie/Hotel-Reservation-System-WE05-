# Serene Hotel Reservation System — SE1020 final report draft

**Team:** [add group number, six real names and student IDs]  
**Repository:** [add actual GitHub URL]  
**Submission date:** [complete]  
**Contribution disclosure:** [complete according to course policy]

This report describes the delivered implementation. Team identity, genuine individual work, commit history, final screenshots and viva evidence must be completed by the students; these cannot be supplied as fabricated evidence.

## 1. Problem and objectives

A hotel's front desk needs consistent guest records, accurate room inventory, conflict-free reservations, staff access control, a service catalogue and a way to collect and moderate feedback. Separate spreadsheets or notebooks make it easy to double-book rooms and lose changes. Serene centralizes these tasks in a Java web application backed by relational SQL, with a responsive staff interface.

The implementation provides all four CRUD operations for each of six modules, more than three UIs, encapsulated Java classes, inheritance hierarchies, overridden behavior and text-file operations. It is designed for six contributors using the package layout from the supplied hotel file-structure blueprint.

## 2. Requirements interpretation

The marking guide weights CRUD (30), OOP (20), file handling (10), UI (10), individual contribution/Git history (10), presentation/viva (10) and class diagrams/final report (10). The project description refers to Java web technology, IntelliJ, sample files and at least three UIs. It includes both file-only wording and an explicit option for a proper SQL database connection. The user's request explicitly requires SQL and full CRUD.

Accordingly, the delivered app uses H2 SQL as the canonical store. It reads six sample `.txt` files at first startup, writes and reads module exports, and writes/reads an activity file. The team should confirm the lecturer's interpretation of the file-handling marks before submission. The sample workload PDF supplies the six-person CRUD distribution pattern; the supplied hotel structure supplies the actual module and inheritance names.

## 3. Technology and structure

Java 17+, Maven, embedded Apache Tomcat 10.1, Jakarta Servlets, JSP, JDBC, H2 and Gson. The UI uses local HTML/CSS/JavaScript with SVG icons. It has no remote font, CDN or frontend build dependency. The prebuilt ZIP contains runtime libraries and launch scripts.

```text
src/main/java/com/hrrs/hotelroomreservationsystem/
  Application.java
  models/     # Entity and six domain hierarchies
  utils/      # SQL repositories, business rules, factories, hashing, file managers
  servlets/   # session/auth, six CRUD controllers, dashboard
src/main/resources/
  schema.sql
  sample-data/guests.txt, rooms.txt, reservations.txt, staff.txt, services.txt, reviews.txt
src/main/webapp/
  index.jsp
  WEB-INF/web.xml
  assets/app.js, styles.css, favicon.svg
src/test/java/ # SQL integration tests
docs/         # ownership, diagrams, API, report, viva and test evidence
```

Request flow: browser fetch → servlet authentication/CSRF/authorization → HotelServiceLayer validation → SqlRepository prepared statements → SQL transaction → JSON result → UI refresh. DomainFactory maps database values to concrete Java subclasses so pricing and display behavior use actual polymorphism. FileManager handles UTF-8 export and audit operations separately from primary persistence.

## 4. Functional modules

**Guest management:** create, list/search, edit and delete guest profiles. Standard/VIP membership determines the available discount. Unique email addresses prevent duplicate guest identities.

**Room management:** add/search/edit/delete room inventory; capacity, base nightly rate, standard/suite type and maintenance are validated. A suite's overridden final-price calculation includes luxury tax. Room inventory availability differs from date-specific booking availability; the latter is checked on reservation save.

**Reservation management:** select existing guest and room, enter dates and party size, save a computed quote, update check-in/out/cancel status and delete inactive records. A serialized single-process transaction checks overlaps and writes the reservation. Stored totals survive unrelated catalogue rate changes. Cancellation fees vary by reservation subclass.

**Staff management:** manager-only staff CRUD with role and shift fields. PBKDF2 password hashes use random salts. Login uses persistent staff records; sessions expire after 30 minutes of inactivity. Authorization rechecks the current staff role on protected requests. Self-deletion and self-demotion are blocked.

**Services and amenities:** CRUD for paid and complimentary amenities, category, status and description. ComplimentaryService.calculateCost always returns zero. This module is an amenities catalogue and does not bill service orders.

**Feedback and reviews:** CRUD and moderation for rating, text and visibility. VerifiedStayReview requires a checked-out reservation, while AnonymousReview has no reservation link and displays an anonymous author. These are staff-entered records; there is no guest authentication or cryptographic proof of guest identity.

## 5. Object-oriented design

| Concept | Implemented example | Practical effect |
|---|---|---|
| Encapsulation | Private fields and constructors/getters in models | Keeps object state behind explicit methods |
| Inheritance | VIPGuest extends Guest; Manager/Receptionist extend Staff | Reuses identity and user properties |
| Polymorphism | Room.calculateFinalPrice, Guest.calculateDiscount, Reservation.calculateCancellationFee | Runtime subclass controls real reservation amounts |
| Abstraction | Abstract Entity/User/Room/Reservation/HotelService/Review and Repository interface | Defines contracts without exposing every implementation |
| Information hiding | Passwords, Database, SqlRepository, FileManager | HTTP/UI code does not implement hashing, connection or file mechanics |

The class and ER diagrams are in CLASS_DIAGRAM.md. Each member's specific polymorphic demonstration is listed in TEAM_WORKLOAD.md.

## 6. SQL and integrity

The database has six domain tables with identity primary keys. Reservations reference guests and rooms; verified reviews reference reservations. Unique keys protect email, room number and service name. CHECK constraints enforce supported enum values, numeric ranges and date ordering. JDBC statements bind user values instead of concatenating them. Table/column names come exclusively from the Module enum.

Writes are transactional. A JVM-wide lock surrounds business checks and writes to prevent two requests in the same process from creating overlapping stays. This deliberately supports one application instance. A clustered deployment would need database-level room locks. Failed validation or SQL operations roll back. Parent deletes fail while foreign-key children remain, preserving history rather than silently cascading.

## 7. File read/write

Database.initialize reads six packaged sample files using UTF-8 readers, parses JSON and inserts the records in a SQL transaction on first run. Each specialized module FileManager inherits safe snapshot writing and reading. Export writes a temporary file, replaces the destination, reads it back and returns a downloadable `.txt` snapshot. Successful CRUD writes append to activity.txt; the dashboard reads a bounded tail. Staff password hashes are excluded from exports. A file write failure does not roll back an already committed SQL operation; server logs record that audit limitation.

## 8. UI design

The visual design uses an ivory background, forest green navigation, muted gold accents, rounded panels and restrained typography. The dashboard shows live occupancy, expected arrivals/departures, guests, room inventory and activity. Each module provides a searchable/filterable list, create route, edit route and confirmed delete dialog. Forms use labeled inputs, numeric ranges and clear error messages. Responsive navigation and single-column mobile forms support smaller devices. All displayed user text is HTML-escaped.

## 9. Validation and results

See TEST_REPORT.md for the actual build and verification results. The automated test suite uses real isolated H2 databases, covers each module's CRUD, and verifies significant domain rules. Browser and HTTP verification supplement those tests. Team members should repeat the demonstration on their own computers before the viva and capture their own final screenshots.

## 10. Limitations and future work

The app is a local teaching project supporting one property and one process. It has no payment gateway, invoice ledger, service ordering, email/SMS integration, guest portal, password recovery or multi-property support. It does not claim production readiness. A future version could add these features, database locks, optimistic concurrency control, pagination and managed backup/restore. Deployment outside localhost requires HTTPS, secure cookies and managed credentials.

## 11. Individual contribution evidence — complete with real work

| Member/student ID | Module | Actual work performed | Pull requests/commits | Demo screenshots |
|---|---|---|---|---|
| [Member 1] | Guests | [complete] | [real links] | [attach] |
| [Member 2] | Rooms | [complete] | [real links] | [attach] |
| [Member 3] | Reservations | [complete] | [real links] | [attach] |
| [Member 4] | Team | [complete] | [real links] | [attach] |
| [Member 5] | Services | [complete] | [real links] | [attach] |
| [Member 6] | Reviews | [complete] | [real links] | [attach] |

Append an actual Git log and repository URL. Each member should be ready to trace one request from UI to servlet, service validation, model method and SQL statement, and explain one file read/write operation.
