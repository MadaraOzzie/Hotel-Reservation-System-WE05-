# Serene Hotel Reservation System

A complete six-member SE1020 Java web project: Jakarta Servlets + JSP, OOP domain classes, responsive HTML/CSS/JavaScript, and UTF-8 file read/write persistence. Java 17 or newer is required. Built with embedded Tomcat 10.1. No database server, Node.js, internet connection, or Maven is required to run the prebuilt copy.

## Quick start (prebuilt)

1. Extract the entire ZIP into a normal writable folder (do not launch from inside the ZIP).
2. Install Java 17+ if needed. Check `java -version`. Set `JAVA_HOME` to your JDK if your computer has multiple Java installations.
3. Windows: double-click `start.bat`. macOS/Linux: run `sh start.sh`.
4. Open **http://localhost:8095**. Keep the terminal open; stop with Ctrl+C.

| Role | Email | Password |
|---|---|---|
| Manager | manager@serene.local | Serene@2026 |
| Receptionist | desk@serene.local | Welcome@2026 |

These are public demonstration accounts. Change their passwords in Team before using real information. Guest records are profiles, not login accounts. This is a staff-operated reservation system.

The packaged app is `target/hotel-reservation-system-1.0.0.jar`; its dependencies are in `target/lib`. Keep `src/main/webapp` in place because the embedded JSP server reads it. The first launch creates `data/store/` and imports the sample rooms plus demo guests, staff, services and one reservation. Later launches keep your changes. Sample stays use October 2026 dates; dashboard arrivals/departures use the server's current date. You can create reservations for other dates.

## IntelliJ IDEA / development

Open `pom.xml` as a Maven project; select JDK 17 or newer. Run `Application.main()` with the project root as the working directory. Or install Maven 3.9+ and run:

```text
mvn clean package
java -jar target/hotel-reservation-system-1.0.0.jar
```

`mvn test` runs the file-storage integration tests. Building from source requires internet access on the first build. The package includes `build.bat` and `build.sh`. The Maven project deliberately uses Servlets/JSP to follow the supplied structure rather than adding an unnecessary framework.

## Six modules / 18 CRUD screens

Every module has its own searchable list, create form, edit form and delete action. Routes are `#/guests`, `#/guests/new`, `#/guests/1/edit`, with the same pattern for all modules.

1. **Guests:** profiles, standard/VIP membership and discount rates.
2. **Rooms:** room numbers, capacity, standard/suite pricing, suite tax and maintenance.
3. **Reservations:** related guest and room, dates, party size, booking type, check-in/out, cancellation, computed total and cancellation fee.
4. **Team:** staff accounts, manager/receptionist role, shift and password changes.
5. **Services:** paid/complimentary dining, wellness, transport and leisure catalogue.
6. **Reviews:** anonymous feedback or verified reviews linked to completed stays, ratings and moderation.

Managers can perform all CRUD operations. Receptionists can perform guest/reservation CRUD, and read rooms/services/reviews. Team access, inventory changes, amenity changes, review moderation and exports require a manager. Backend authorization applies even when someone bypasses UI controls.

## Important domain rules

- Stay dates use `[check-in, check-out)`, so a departure and another arrival on the same day can share a room. Active overlapping reservations are rejected on create/update. Cancelled/completed bookings do not block future availability.
- Party size cannot exceed room capacity. Maintenance rooms cannot accept active bookings. Rooms with active bookings cannot enter maintenance or reduce capacity.
- Check-in is allowed during the stay dates. Check-out cannot precede arrival. Staff can record historical completed stays.
- Total = nights × (room rate + suite tax) × VIP discount. Money uses `BigDecimal`. Unchanged dates, guest, room and booking type retain the agreed reservation total even if catalogue rates change.
- Advance cancellation fee is 10% of the stored total. Walk-in cancellation fee is zero. These policies demonstrate polymorphism; they do not process actual payments.
- Active reservations must be cancelled/completed before deletion. Linked guests, rooms, reservations and reviews are protected by service-level checks. Remove linked reviews before deleting their stay. Anonymous reviews have no reservation link; verified reviews require a checked-out stay.
- Managers cannot delete or demote their own signed-in account. Passwords use salted PBKDF2-HMAC-SHA256 and are omitted from API responses/exports.

## File storage and exports

The canonical store is one UTF-8 JSON file per module in `data/store/` (`guests.json`, `rooms.json`, `reservations.json`, `staff.json`, `services.json`, `reviews.json`). Each write is serialized under a JVM-wide lock and written to a temporary file before an atomic move replaces the destination. A marker file (`data/store/.seeded`) prevents deleted demo records from reappearing on restart.

On first launch, `rooms.txt` from the classpath supplies room seed data. Demo guests, staff, services and one reservation are added when their module file is empty. If `src/main/resources/sample-data/<module>.txt` exists, it is imported first.

Each directory has **Export .txt**. Its module-specific FileManager writes a UTF-8 snapshot to `data/exports`, reads the file back, then downloads it. Staff exports omit password hashes. `data/activity.txt` records successful CRUD actions; the dashboard reads its latest eight entries. Exported files are snapshots; copy `data/store/` to back up the application data.

The service layer enforces unique emails/room numbers/service names and referential checks because plain files do not provide database constraints. Writes are atomic per module, but this design intentionally targets a single running application instance. Use a database if multiple server instances must share writes.

## Configuration

| Environment variable | Default | Purpose |
|---|---|---|
| PORT | 8095 | HTTP port |
| HOTEL_BIND | 127.0.0.1 | Listen address |

Example PowerShell: `$env:PORT='8081'` then `./start.bat`.

The application binds to your computer by default. A real hosted deployment needs HTTPS, secure cookies, managed secrets and centralized audit storage. The present overlap protection serializes writes within one application process. Run only one instance against a data directory. The UI requires a modern browser and JavaScript. Services are a catalogue; service orders, payment processing, guest self-service, emails and multi-property operation are outside this project's scope.

## Documentation

- `docs/TEAM_WORKLOAD.md`: six-person ownership, exact files, UI/CRUD/OOP responsibilities and integration plan.
- `docs/CLASS_DIAGRAM.md`: OOP, architecture and entity relationship diagrams.
- `docs/FINAL_REPORT.md`: implementation report and fields the team must complete with its own evidence.
- `docs/VIVA_GUIDE.md`: demonstration sequence and backend explanation prompts.
- `docs/API.md`: endpoints, authorization and request examples.
- `docs/TEST_REPORT.md`: verified build and test results.

Do not submit invented contributions or commit history. Assign member names, review the code together, make genuine improvements in your own branches, and add your real Git history and screenshots to the report.

## Troubleshooting

- **Java command missing:** install JDK 17+ and set `JAVA_HOME` or add its `bin` folder to PATH.
- **Port already in use:** stop the other server or change PORT.
- **Data file unreadable:** stop the server, inspect `data/store/<module>.json`, and restore it from a backup.
- **Cannot delete:** cancel/complete the active reservation and remove linked child records first.
- **Manager cannot sign in:** passwords are case-sensitive; check the account email. After five failures, wait one minute.
- **Want fresh demo data:** stop the app, back up your existing data, then remove `data/store/` and restart.
- **JSP not found:** launch from the extracted project root and keep `src/main/webapp` with the application.

## Dependency references

[Apache Tomcat 10.1 documentation](https://tomcat.apache.org/tomcat-10.1-doc/) and [Gson documentation](https://github.com/google/gson). See `THIRD_PARTY_NOTICES.md` for bundled dependency licenses.
