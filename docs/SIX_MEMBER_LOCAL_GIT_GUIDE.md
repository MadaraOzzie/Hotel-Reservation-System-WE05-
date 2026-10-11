# Six-member local setup, Git collaboration and module demonstration guide

This guide assumes Windows and PowerShell. Every member has their own PC, GitHub account and local project. On macOS/Linux use `sh start.sh` in place of `.\start.bat`; the Git/Maven commands are the same.

## 1. What the PDFs actually require

The supplied **Marking Guide.pdf** assigns 10 marks to individual contribution and GitHub commit history as evidence of individual work. **SE1020 - Project Description.pdf**, page 1, requires GitHub collaboration, a final report containing Git repository commit history, presentation of that history, and each person explaining/demonstrating their contribution. It also emphasizes backend Java, OOP and file handling in the viva. Page 2 specifies at least three UIs and accepts at least three CRUD operations per individual; this project supplies all four.

The supplied PDFs do **not** prescribe a minimum number of commits, a commit every day, branch names, commit-message format or a particular pull-request process. The workflow below is a recommendation for producing clear, genuine evidence; these details are not additional lecturer rules.

Because a completed starter project has already been supplied, uploading its files does not establish that each member authored them. Record the provided baseline honestly, follow your institution's assistance/disclosure policy, and have each member perform, understand and document real work on their module. Do not fabricate, backdate or split unchanged files merely to inflate commit counts.

## 2. Understand the ZIP before starting

Download **Hotel-Six-Categories.zip** and use **Extract All** on each PC. Do not run the application inside the ZIP.

```text
Hotel-Six-Categories/
  START-HERE.md
  00-Shared-Core/
  01-Guest-Management/
  02-Room-Management/
  03-Reservation-Management/
  04-Staff-Admin-Management/
  05-Services-Amenities/
  06-Feedback-Reviews/
  Complete-Runnable-Project/
    pom.xml
    start.bat
    src/
    target/
    docs/
```

Use **Complete-Runnable-Project** for your first launch. The numbered folders are ownership/reference packages. They contain copies of files already integrated into the complete project. Do not open all seven copies as Java source roots, and do not paste the category folder names into `src`.

Once the team repository exists, do your development in its **clone**, not in an old extracted copy. Keep the extracted ZIP as a reference.

## 3. Install the tools on all six PCs

Install these before collaborative development:

1. JDK 17 or newer; JDK 17 or 21 is suitable. You need a JDK, not just a Java runtime.
2. Git for Windows, and a separate GitHub account for each person.
3. IntelliJ IDEA with Java/Maven support.
4. Apache Maven 3.9+ for the PowerShell build commands in this guide. Add its `bin` directory to PATH and configure JAVA_HOME to your JDK. IntelliJ's bundled Maven is an alternative if you run the Maven goals through its Maven tool window.
5. A modern browser.

Open a **new** PowerShell window after installing/configuring tools and check:

```powershell
java -version
javac -version
git --version
mvn -v
```

The Java compiler must be 17+. Maven should report the intended JDK. If Java installations disagree, fix JAVA_HOME/PATH before continuing. Installation instructions: [Maven](https://maven.apache.org/install.html), [Git for Windows](https://git-scm.com/install/windows).

You do not need MySQL, XAMPP, Node.js or a separate Tomcat installation. H2 SQL and Tomcat are Java dependencies. Running the extracted prebuilt copy needs Java only; building a Git clone needs Maven and internet on the first build.

## 4. First launch from the extracted ZIP — everyone

1. Open the extracted `Complete-Runnable-Project` folder.
2. Confirm that `pom.xml`, `start.bat`, `src` and `target` are visible together.
3. Open PowerShell in that folder, or open the folder in File Explorer, type `powershell` in its address bar and press Enter.
4. Run:

```powershell
.\start.bat
```

5. Wait for `Serene Hotel is ready: http://localhost:8095`.
6. Open **http://localhost:8095**.
7. Sign in with `manager@serene.local` / `Serene@2026` so every category's create/edit/delete actions are available.
8. Keep the terminal open. Press **Ctrl+C** there to stop the server before rebuilding.

The first launch creates `data/hotel.mv.db`, reads the six sample `.txt` files and populates the SQL tables. There is no manual database import step. Each person's database stays on their own PC. Localhost on Member 1's PC is different from localhost on Member 2's PC, so all six may use port 8095 simultaneously on separate PCs.

If 8095 is occupied on a particular PC, choose a free port, for example:

```powershell
$env:PORT='8195'
.\start.bat
```

Then open http://localhost:8195. This sets the port for processes launched from that PowerShell window. Do not stop unrelated applications to free a port.

## 5. Create one team repository — leader only

The team needs **one shared repository**, not six unrelated repositories. Choose one leader to create it; everyone, including the leader, uses their own identity for their contributions.

1. Copy `Complete-Runnable-Project` into a normal development location and rename it `serene-hotel`. Avoid OneDrive or other live-sync folders for the active database.
2. On GitHub, create an **empty** repository, for example `serene-hotel-reservation`. Follow your lecturer's visibility/access requirements. Do not generate a README or .gitignore on GitHub because the local project already has them.
3. Open PowerShell inside `serene-hotel` and configure your real identity. Replace the sample values:

```powershell
git init -b main
git config user.name "YOUR REAL NAME"
git config user.email "YOUR VERIFIED GITHUB EMAIL"
git status
```

4. Review `.gitignore`. It excludes `target/`, `data/`, IDE files and logs. Do not remove these exclusions to make the Git clone runnable; the build recreates `target` and launch recreates `data`.
5. Record the supplied project as a baseline:

```powershell
git add .
git diff --cached --stat
git commit -m "chore: import provided hotel project baseline"
git remote add origin "https://github.com/YOUR-TEAM/serene-hotel-reservation.git"
git push -u origin main
```

Replace the entire example URL with the repository's actual HTTPS URL. GitHub may open an authentication prompt; use your own account. Do not enter your GitHub password into source code.

6. Add the other five accounts as collaborators and have them accept the invitations. GitHub's current steps are in [Inviting collaborators](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/repository-access-and-collaboration/inviting-collaborators-to-a-personal-repository).
7. Agree that feature changes go through pull requests. If available for your repository plan, configure branch protection requiring reviews on `main`.

The baseline import is not six people's individual implementation history. Subsequent genuine changes, reviews and tests establish each member's contribution.

## 6. Clone the team repository — other five members

After accepting the invitation, open PowerShell in the parent folder where you want your project:

```powershell
git clone "https://github.com/YOUR-TEAM/serene-hotel-reservation.git" serene-hotel
cd serene-hotel
git config user.name "YOUR REAL NAME"
git config user.email "YOUR VERIFIED GITHUB EMAIL"
git config --get user.name
git config --get user.email
git remote -v
```

Use your own real name and GitHub-verified email. Repository-local `git config` avoids changing your identity in unrelated projects. The leader already has this working repository and does not need to clone it again.

The clone will **not contain `target/` or `data/`**. That is expected. Build it:

```powershell
mvn clean package
.\start.bat
```

The first Maven build downloads dependencies. Later offline builds are possible once everything is cached. If the build fails, read the error and fix it rather than skipping the tests by default.

## 7. Open the correct project in IntelliJ — everyone

1. Open the clone's `pom.xml` and choose to open it as a project.
2. Select JDK 17+ as the Project SDK and Maven runner JDK.
3. Let Maven finish importing dependencies.
4. Open `src/main/java/com/hrrs/hotelroomreservationsystem/Application.java`.
5. Run `Application.main()` with the **project root** as working directory.
6. Open http://localhost:8095.

Use either IntelliJ Run **or** start.bat at one time. Starting both can cause a port conflict or database lock. If using IntelliJ's bundled Maven, execute `clean package` through the Maven tool window instead of the PowerShell Maven command.

## 8. Create your assigned branch — everyone

Start from a clean, up-to-date `main`:

```powershell
git switch main
git pull --ff-only origin main
```

Then execute **only your own row's command**:

| Member | Category | Branch command | Module address after sign-in |
|---|---|---|---|
| 1 | Guests | `git switch -c feature/guests` | http://localhost:8095/#/guests |
| 2 | Rooms | `git switch -c feature/rooms` | http://localhost:8095/#/rooms |
| 3 | Reservations | `git switch -c feature/reservations` | http://localhost:8095/#/reservations |
| 4 | Staff/Admin | `git switch -c feature/staff` | http://localhost:8095/#/staff |
| 5 | Services/Amenities | `git switch -c feature/services` | http://localhost:8095/#/services |
| 6 | Feedback/Reviews | `git switch -c feature/reviews` | http://localhost:8095/#/reviews |

Replace 8095 in the addresses if you chose another port. These are recommended branch names, not names mandated by the PDFs.

## 9. How to run a member's module separately

**The categories are integrated modules, not six standalone servers.** Do not attempt `java GuestServlet.java`, run a model's class or open the UI `.js` file in the browser. Servlets need the HTTP server, session controller, JDBC layer and other shared classes. Reservations also need guests/rooms; verified reviews need reservations.

Each member can work and demonstrate independently on their own PC by:

1. Building and starting the **whole local app** from their own Git clone/branch.
2. Signing in as manager.
3. Opening only their module address from the table above.
4. Editing their assigned files and coordinating any shared-file changes.
5. Demonstrating their module's list, create, edit and delete actions.

Other modules remain available as dependencies; no teammate's PC or server is required. Everyone's app and SQL data are independent. No shared remote database is used.

Optionally use a separate local demonstration database. With the server stopped, run the following from your project root, replacing `guests-demo` with your category:

```powershell
$env:HOTEL_DB_URL='jdbc:h2:file:./data/guests-demo;DB_CLOSE_ON_EXIT=FALSE'
.\start.bat
```

This creates `data/guests-demo.mv.db` with all six tables and fresh sample data. Changing the database name isolates data, not the loaded modules. Close that PowerShell window and open a new one to return to the default `data/hotel.mv.db`. Keep only one instance per database.

## 10. Exact files each person should work on

All Java files below are under `src/main/java/com/hrrs/hotelroomreservationsystem/`.

| Member | Java models | Backend/file handling | Own UI file |
|---|---|---|---|
| 1 | `models/User.java`, `Guest.java`, `VIPGuest.java` | `servlets/GuestServlet.java`, `utils/GuestFileManager.java` | `src/main/webapp/assets/modules/guests.js` |
| 2 | `models/Room.java`, `StandardRoom.java`, `SuiteRoom.java` | `servlets/RoomServlet.java`, `utils/RoomFileManager.java` | `src/main/webapp/assets/modules/rooms.js` |
| 3 | `models/Reservation.java`, `AdvanceBooking.java`, `WalkInBooking.java` | `servlets/ReservationServlet.java`, `utils/ReservationFileManager.java` | `src/main/webapp/assets/modules/reservations.js` |
| 4 | `models/Staff.java`, `Manager.java`, `Receptionist.java` | `servlets/StaffServlet.java`, `AuthServlet.java`, `utils/StaffFileManager.java`, `Passwords.java` | `src/main/webapp/assets/modules/staff.js` |
| 5 | `models/HotelService.java`, `PaidService.java`, `ComplimentaryService.java` | `servlets/ServiceServlet.java`, `utils/ServiceFileManager.java` | `src/main/webapp/assets/modules/services.js` |
| 6 | `models/Review.java`, `VerifiedStayReview.java`, `AnonymousReview.java` | `servlets/ReviewServlet.java`, `utils/ReviewFileManager.java` | `src/main/webapp/assets/modules/reviews.js` |

Models in each cell share the `models/` prefix. Each member also owns `src/main/resources/sample-data/<module>.txt` for their category. Sample files are read only on a new database's first initialization; changing one does not overwrite an existing database.

The category packages also contain `SQL/<module>.sql` for reference. **The runtime schema is `src/main/resources/schema.sql`**, with a reference copy at `database/schema.sql`. Editing only a category's reference SQL file will not change the application database. Coordinate schema changes with the team; the present CREATE TABLE IF NOT EXISTS initialization is not a migration system for altering existing columns.

Important shared files:

- `utils/HotelServiceLayer.java`: each module's validation branch, transactions and CRUD orchestration.
- `utils/SqlRepository.java`: shared prepared SQL queries.
- `utils/DomainFactory.java`: SQL rows to domain subclasses.
- `servlets/ApiServlet.java` and `CrudServlet.java`: session/role/CSRF checks and HTTP CRUD mechanics.
- `assets/app.js` and `styles.css`: shared screen rendering, navigation and styling.

These shared implementations are part of the project. A short module servlet delegates to common code; each member must understand the actual validation/SQL path, not only the small wrapper. Tell your teammates before editing a shared file and keep changes focused on your module.

## 11. Demonstrate each member's CRUD and OOP

Use your own local database and manager account. Start with a list screenshot, then create, read/search, update and delete a temporary record. Capture your own screenshots rather than claiming the provided screenshots as your personal testing evidence.

**Member 1 — Guests:** create a guest with a unique email; select VIP and discount `0.10`; search for the guest; edit contact details and discount; export `.txt`; delete the guest after deleting any linked reservations. Explain `User -> Guest -> VIPGuest`, private fields/getters and `calculateDiscount`.

**Member 2 — Rooms:** create a suite with capacity 4, base price 10000 and tax `0.10`; confirm nightly rate 11000; edit rate/status; search and export; delete after removing reservations. Explain `Room -> StandardRoom/SuiteRoom` and overridden final pricing. Do not set an actively reserved room to maintenance.

**Member 3 — Reservations:** create a VIP guest and suite locally or use existing ones. Book two non-overlapping nights for a 10%-discount VIP guest in the 10000 + 10%-tax suite: total should be **19800**. Attempt an overlapping active reservation to show rejection. Edit dates or party size, then cancel; an advance booking has a **1980** cancellation fee when the total is 19800. Delete the cancelled booking after removing any linked reviews. Explain AdvanceBooking/WalkInBooking, BigDecimal, date ranges and transaction protection. Check-in requires the current date to fall inside the stay.

**Member 4 — Staff/Admin:** create a temporary receptionist with a new email and 10+ character password. Read it in the directory; update shift/password; test its sign-in using a separate browser profile/private window. Verify that staff CRUD and room writes are unavailable to that receptionist. Delete the temporary account while signed in as the manager. Do not delete/demote the account you are using. Explain Staff inheritance, role polymorphism, password hashing and backend authorization.

**Member 5 — Services:** add a paid amenity; search/read it; edit type to complimentary and confirm effective price is zero; export; delete the temporary amenity. Explain HotelService abstraction and the overridden zero-cost calculation. This module is an amenities catalogue; it does not create service orders or take payments.

**Member 6 — Reviews:** create anonymous feedback, search and edit its rating/text, hide/publish it and export. Create a verified review linked to a CHECKED_OUT reservation; sample booking 3 is a completed stay on a fresh database. Show rejection for a verified review linked to an active booking. Delete your temporary reviews. Explain Review subclasses, display tags and reservation foreign keys.

For every category, show the relevant exported `.txt` and explain the Java file read/write methods. SQL remains primary storage; the supplied documents contain both file-only wording and an SQL option, so the team should confirm the lecturer's interpretation of separately weighted file handling.

## 12. Daily coding and genuine commits

At the start of a session, with existing work committed and the server stopped when rebuilding:

```powershell
git status
git fetch origin
git switch feature/guests
git merge origin/main
```

Use your actual branch name. If `git status` shows unfinished work, finish/commit it before changing branches or merging.

Make a real change you can explain: a needed business-rule improvement, a bug fix, a meaningful regression test, better module UI behavior, or your own accurate module documentation/test evidence. Already supplied CRUD code must not be relabeled as newly authored work.

After Java or resource changes:

```powershell
mvn package
.\start.bat
```

Stop the old server with Ctrl+C before building/launching the replacement. `mvn test` alone runs tests but does not refresh the packaged JAR. For CSS/JavaScript-only changes, a browser refresh normally suffices; use Ctrl+F5 if caching interferes.

Inspect and commit a completed piece of real work. Example for Member 1 after actually editing the guest UI and writing their own test notes:

```powershell
git status
git diff
git add -- src/main/webapp/assets/modules/guests.js docs/guest-test-notes.md
git diff --cached
git commit -m "feat(guests): improve membership form guidance"
git push -u origin feature/guests
```

The example documentation file must be created by you before staging it. Adapt the staged paths and commit message to the changes actually made. On later pushes to the tracked branch, `git push` is enough.

Do not commit `data/`, `target/`, local passwords or another person's unfinished changes. Keep your own real Git identity. Push completed work regularly so GitHub has a visible history; no specific daily frequency or commit count is mandated by the supplied PDFs.

## 13. Pull requests and integration

1. Push your branch.
2. On GitHub open a pull request from your feature branch to `main`.
3. Explain the problem/change, files affected and actual validation performed. Include module screenshots when useful.
4. Ask another member to review it and test relevant flows.
5. Resolve any requested changes on your own branch with additional genuine commits.
6. Run `mvn test` and relevant manual module checks after integration changes.
7. Merge using **Create a merge commit** to retain individual commits in the main history. For this assignment, avoid squash-merging unless you preserve other contribution evidence and the team agrees. This is a recommendation for traceability, not a PDF requirement.
8. Other members fetch and merge the new main into their active branch before continuing shared-file work.

GitHub's instructions: [Creating a pull request](https://docs.github.com/en/pull-requests/creating-and-managing-pull-requests/creating-a-pull-request).

If a conflict occurs, read the files with conflict markers, discuss the intended combined behavior with the other author, edit a correct resolution, stage only resolved files, test, then commit the merge and push. Do not blindly replace another member's version or force-push shared history.

## 14. What each member should save as assessment evidence

- Their real GitHub account/name and assigned module.
- Links to their own meaningful commits and pull requests.
- Their actual role in reviews and shared-code changes.
- Screenshots of their three screens and CRUD demonstrations.
- A brief validation log with date, input, expected result and observed result.
- One OOP trace showing the overridden method called by the running application.
- One SQL trace and one text-file read/write trace.

Put factual notes in a member/module documentation file and complete the real identity/contribution table in `docs/FINAL_REPORT.md`. Use your actual commit history rather than a manually invented table.

## 15. Final submission and viva preparation

After all reviewed work is merged, each member checks the final version:

```powershell
git switch main
git pull --ff-only origin main
mvn clean package
.\start.bat
```

Repeat the six module demonstrations. Then stop the app and export history:

```powershell
git fetch --all
git log main --date=short --pretty=format:"%h | %ad | %an | %s" > docs/git-history.txt
git shortlog -sne main
```

The log records genuine main-branch history at the time of export. After committing the final report/history, include the repository URL and final commit hash in your submission; it is normal that the log snapshot precedes its own documentation commit. Do not treat `git shortlog` counts as proof of quality; reviewers need the actual changes.

The final report should include the real six-person allocation, class diagrams, test evidence, contribution links and Git history. Every member should be ready to trace their UI request through servlet, shared service, OOP method, SQL statement and file operation. The supplied project does not by itself guarantee marks or satisfy the individual contribution/viva requirements.

## 16. Quick troubleshooting

| Symptom | Action |
|---|---|
| `mvn` not recognized | Install/configure Maven PATH or use IntelliJ's Maven tool window |
| Java version/compiler error | Check `java -version`, `javac -version`, `mvn -v`, and IntelliJ's JDK |
| Missing `target` after cloning | Run `mvn clean package` |
| Old Java behavior after editing | Stop server, run `mvn package`, restart |
| Port in use | Set a free PORT in the launch terminal |
| Database locked | Stop your duplicate application instance; one process per database |
| No module edit/delete buttons | Sign in as manager; receptionist access is restricted |
| Cannot delete record | Remove linked children first; cancel/complete active bookings |
| Changed sample file does not affect current data | It seeds only a new database; use a new demo database name |
| Cannot push | Accept collaborator invitation, verify remote URL and authenticate with your own account |
| Wrong Git author | Check repo-local user.name/user.email before committing; do not rewrite shared history casually |
| SQL file edit had no effect | Edit the runtime schema, rebuild and use a fresh test database or implement a migration |

The six-person setup does not require six ports, a shared database, six Git repositories or six independently deployed servers. It requires six genuine contributors working on one integrated project, with separate local environments and a shared GitHub history.
