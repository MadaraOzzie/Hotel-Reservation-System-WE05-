# SQL database code

`schema.sql` contains the complete H2 SQL DDL for guests, rooms, reservations, staff, services and reviews, with keys, constraints and an availability index.

The runtime reads the identical canonical file at `src/main/resources/schema.sql`. If changing the schema, edit that resource, refresh this reference copy, and rebuild.

Java SQL code is under `src/main/java/com/hrrs/hotelroomreservationsystem/utils/`:

- `Database.java`: JDBC connection, schema execution and initial sample-data import.
- `SqlRepository.java`: parameterized SELECT, INSERT, UPDATE and DELETE statements.
- `HotelServiceLayer.java`: validation, reservation overlap query and transactions.

`servlets/AuthServlet.java` contains the staff email lookup for login.

The six sample-data files are in `src/main/resources/sample-data/`. Their data is inserted through JDBC prepared statements on the first launch. No manual SQL import is needed.

The actual persistent SQL database is `data/hotel.mv.db`, created at runtime. Stop the application before copying this file for a backup. The ZIP excludes test databases and starts with clean sample data.

This is H2 SQL, not a MySQL dump. MySQL would require its JDBC driver and schema/initialization changes.
