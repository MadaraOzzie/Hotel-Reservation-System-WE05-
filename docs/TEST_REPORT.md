# Verification report

Verified on 11 October 2026 using Java 19 (compiling to Java 17), Maven 3.9.6, embedded Tomcat 10.1.60, H2 2.3.232 and Chromium Headless Shell. The running test server used port 8093 and a separate SQL database, which is excluded from the deliverable.

## Build and automated SQL tests

`mvn package`: **BUILD SUCCESS**. Final Java package builds offline after initial dependency download. **10 tests, 0 failures, 0 errors, 0 skipped.** See `test-evidence.txt` for the test runner output.

1. Create, read, update and delete records in all six modules; SQL persistence and suite/VIP price calculation.
2. Reject overlapping active stays and accept adjacent stays.
3. Cancellation releases availability and computes the advance-booking fee.
4. Reject zero-night dates and excessive party size.
5. Enforce foreign keys, signed-in account self-protection and active reservation deletion protection.
6. Verified reviews require a completed stay.
7. Password hashes are salted, verified correctly and excluded from list responses.
8. All six UTF-8 file exports round-trip without password hashes.
9. Duplicate guest email fails without increasing the record count.
10. Complimentary amenities have zero effective charge.

## Live HTTP and browser checks

All **11 check groups passed**; **zero JavaScript page errors**. Raw results are in `browser-results.json`, and the optional reproducible browser script is `tests/browser-smoke.cjs`.

- Manager sign-in and JSP/dashboard render.
- HTTP create/read/update for all six resources; expected LKR 19,800 suite/VIP total.
- Missing CSRF token rejected with 403.
- Booking overlap rejected; active booking deletion rejected; foreign-key conflict returned as 409; cancellation and deletion succeed.
- All 18 list/create/edit routes render.
- Real guest UI create/search/edit/confirmed-delete sequence succeeds.
- All six export downloads are parseable JSON text with no password hashes.
- Desktop at 1440px and mobile at 390px; mobile dashboard/form have no page-width overflow. Wide data tables scroll within their panels.
- HTTP deletion succeeds for all six temporary resource types in dependency order.
- Receptionist login succeeds; staff data and inventory writes return 403; guest reads succeed.
- Unauthenticated data read returns 401.

The mobile check initially exposed a grid minimum-width issue; it was corrected and the complete browser suite passed afterward. An asynchronous UI-delete test wait was also corrected. A final form-description copy edit does not alter behavior.

## Visual evidence

`screenshots/01-login.png`, `02-dashboard.png`, `03-reservations.png`, `04-reservation-form.png`, `05-mobile-dashboard.png`, `06-mobile-form.png`. These are captures of the working app; some contain temporary browser test records. The delivered application starts with clean sample data rather than the test database.

## Limits of verification

Tests verify a single-process local application and a modern Chromium browser. They do not constitute a production security audit, concurrent multi-instance test, MySQL compatibility test, payment integration test or Safari/Firefox certification. The project must be reviewed and demonstrated by each team member, and the course's SQL/file-handling interpretation should be confirmed with the lecturer.

## Six-category distribution verification

The module UI configurations were moved into six separate `assets/modules/*.js` files without changing their contents. A JavaScript VM comparison confirmed all six configurations exactly match the original working UI. Script load order and JavaScript syntax were verified. Java backend code is unchanged. All category ZIPs and the combined archive passed ZIP integrity checks. The earlier browser suite was performed before this configuration-only extraction.
