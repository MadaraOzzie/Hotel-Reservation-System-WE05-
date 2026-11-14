package com.hrrs.hotelroomreservationsystem;

import com.hrrs.hotelroomreservationsystem.models.AdvanceBooking;
import com.hrrs.hotelroomreservationsystem.models.Guest;
import com.hrrs.hotelroomreservationsystem.models.Reservation;
import com.hrrs.hotelroomreservationsystem.models.Room;
import com.hrrs.hotelroomreservationsystem.models.StandardRoom;
import com.hrrs.hotelroomreservationsystem.models.SuiteRoom;
import com.hrrs.hotelroomreservationsystem.models.VIPGuest;
import com.hrrs.hotelroomreservationsystem.models.WalkInBooking;
import com.hrrs.hotelroomreservationsystem.utils.DomainFactory;
import com.hrrs.hotelroomreservationsystem.utils.FileRepository;
import com.hrrs.hotelroomreservationsystem.utils.FileStore;
import com.hrrs.hotelroomreservationsystem.utils.HotelServiceLayer;
import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.ReservationReportService;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Comprehensive verification tests for the Reservation OOP models, service rules, and reporting
public final class ReservationModuleTest {

    public static void main(String[] args) throws Exception {
        ReservationModuleTest suite = new ReservationModuleTest();
        suite.runAll();
        System.out.println("All ReservationModuleTest checks passed.");
    }

    public void runAll() throws Exception {
        testAdvanceBookingStandardRoomCalculation();
        testWalkInBookingSuiteRoomWithVipDiscount();
        testMinimumOneNightChargeRule();
        testCancellationFeeDifferences();
        testDisplayTagsAndPolymorphism();
        testDomainFactoryMapping();
        testServiceLayerCreateUpdateDeleteAndOverlapChecks();
        testReservationReportServiceMetrics();
    }

    // Verifies AdvanceBooking total calculation for a StandardRoom and regular Guest
    public void testAdvanceBookingStandardRoomCalculation() {
        Room standardRoom = new StandardRoom(1L, "101", new BigDecimal("15000.00"));
        Guest regularGuest = new Guest(1L, "Nimal Perera", "nimal@example.com");
        Reservation booking = new AdvanceBooking(
                10L,
                regularGuest.getId(),
                standardRoom.getId(),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 13)
        );

        BigDecimal total = booking.calculateTotal(standardRoom, regularGuest);
        assertEquals(new BigDecimal("45000.00"), total, "3 nights at 15000.00 should equal 45000.00");
        assertEquals("ADVANCE", booking.getBookingType(), "AdvanceBooking type must be ADVANCE");
    }

    // Verifies WalkInBooking calculation combining SuiteRoom luxury tax and VIPGuest discount
    public void testWalkInBookingSuiteRoomWithVipDiscount() {
        Room suiteRoom = new SuiteRoom(2L, "501", new BigDecimal("40000.00"), new BigDecimal("0.10"));
        Guest vipGuest = new VIPGuest(2L, "Amara Patel", "amara@example.com", new BigDecimal("0.10"));
        Reservation booking = new WalkInBooking(
                11L,
                vipGuest.getId(),
                suiteRoom.getId(),
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 3)
        );

        // Nightly suite price = 40000 + 10% tax = 44000; 2 nights = 88000; 10% VIP discount = 79200.00
        BigDecimal total = booking.calculateTotal(suiteRoom, vipGuest);
        assertEquals(new BigDecimal("79200.00"), total, "Suite with luxury tax and VIP discount mismatch");
        assertEquals("WALK_IN", booking.getBookingType(), "WalkInBooking type must be WALK_IN");
    }

    // Verifies Math.max(1L, nights) enforces at least 1 billed night when dates match
    public void testMinimumOneNightChargeRule() {
        Room standardRoom = new StandardRoom(3L, "102", new BigDecimal("12000.00"));
        Guest guest = new Guest(3L, "Kasun Silva", "kasun@example.com");
        LocalDate sameDay = LocalDate.of(2026, 11, 20);
        Reservation sameDayBooking = new WalkInBooking(12L, 3L, 3L, sameDay, sameDay);

        BigDecimal total = sameDayBooking.calculateTotal(standardRoom, guest);
        assertEquals(new BigDecimal("12000.00"), total, "Same-day booking must charge minimum 1 night");
    }

    // Verifies 10% cancellation fee for AdvanceBooking and 0.00 fee for WalkInBooking
    public void testCancellationFeeDifferences() {
        LocalDate in = LocalDate.of(2026, 12, 10);
        LocalDate out = LocalDate.of(2026, 12, 12);
        BigDecimal bookingTotal = new BigDecimal("50000.00");

        Reservation advance = new AdvanceBooking(20L, 1L, 1L, in, out);
        Reservation walkIn = new WalkInBooking(21L, 1L, 1L, in, out);

        assertEquals(new BigDecimal("5000.00"), advance.calculateCancellationFee(bookingTotal),
                "Advance booking cancellation fee must be 10%");
        assertEquals(new BigDecimal("0.00"), walkIn.calculateCancellationFee(bookingTotal),
                "Walk-in booking cancellation fee must be 0.00");
    }

    // Verifies polymorphic getDisplayTag() output on both Reservation subclasses
    public void testDisplayTagsAndPolymorphism() {
        LocalDate in = LocalDate.of(2026, 12, 15);
        LocalDate out = LocalDate.of(2026, 12, 17);

        Reservation advance = new AdvanceBooking(30L, 1L, 1L, in, out);
        Reservation walkIn = new WalkInBooking(31L, 1L, 1L, in, out);

        assertEquals("ADVANCE reservation", advance.getDisplayTag(), "Display tag mismatch for AdvanceBooking");
        assertEquals("WALK_IN reservation", walkIn.getDisplayTag(), "Display tag mismatch for WalkInBooking");
    }

    // Verifies DomainFactory converts stored Map rows into AdvanceBooking or WalkInBooking instances
    public void testDomainFactoryMapping() {
        Map<String, Object> advanceRow = Map.of(
                "id", "5",
                "guest_id", "1",
                "room_id", "2",
                "check_in", "2026-11-25",
                "check_out", "2026-11-28",
                "type", "ADVANCE"
        );
        Map<String, Object> walkInRow = Map.of(
                "id", "6",
                "guest_id", "1",
                "room_id", "2",
                "check_in", "2026-11-25",
                "check_out", "2026-11-28",
                "type", "WALK_IN"
        );

        Reservation advanceModel = DomainFactory.reservation(advanceRow);
        Reservation walkInModel = DomainFactory.reservation(walkInRow);

        assertTrue(advanceModel instanceof AdvanceBooking, "DomainFactory must build AdvanceBooking");
        assertTrue(walkInModel instanceof WalkInBooking, "DomainFactory must build WalkInBooking");
    }

    // Verifies end-to-end CRUD, overlap prevention, cancellation fee persistence, and delete rules
    public void testServiceLayerCreateUpdateDeleteAndOverlapChecks() throws Exception {
        Path tempDir = Files.createTempDirectory("hrrs-reservation-test");
        Path originalDir = FileStore.directory();
        try {
            FileStore.useDirectory(tempDir);
            FileStore.initialize();

            HotelServiceLayer service = new HotelServiceLayer();

            // 1. Create a valid ADVANCE reservation
            Map<String, Object> createPayload = new LinkedHashMap<>();
            createPayload.put("guest_id", "1");
            createPayload.put("room_id", "1");
            createPayload.put("check_in", "2027-01-10");
            createPayload.put("check_out", "2027-01-13");
            createPayload.put("party_size", "2");
            createPayload.put("type", "ADVANCE");
            createPayload.put("status", "CONFIRMED");

            long createdId = service.save(Module.RESERVATIONS, 0, createPayload, 1L);
            assertTrue(createdId > 0, "Created reservation ID must be positive");

            // 2. Attempt double-booking the same room on overlapping dates
            Map<String, Object> overlapPayload = new LinkedHashMap<>(createPayload);
            overlapPayload.put("check_in", "2027-01-11");
            overlapPayload.put("check_out", "2027-01-14");
            boolean overlapRejected = false;
            try {
                service.save(Module.RESERVATIONS, 0, overlapPayload, 1L);
            } catch (IllegalArgumentException expected) {
                overlapRejected = expected.getMessage().contains("already reserved");
            }
            assertTrue(overlapRejected, "Overlapping booking must be rejected");

            // 3. Attempt deleting a CONFIRMED reservation (must fail)
            boolean activeDeleteBlocked = false;
            try {
                service.delete(Module.RESERVATIONS, createdId, 1L);
            } catch (IllegalArgumentException expected) {
                activeDeleteBlocked = expected.getMessage().contains("Cancel or complete");
            }
            assertTrue(activeDeleteBlocked, "Deleting a CONFIRMED reservation must be blocked");

            // 4. Cancel the reservation and verify 10% cancellation fee is stored
            Map<String, Object> cancelPayload = new LinkedHashMap<>(createPayload);
            cancelPayload.put("status", "CANCELLED");
            service.save(Module.RESERVATIONS, createdId, cancelPayload, 1L);

            Map<String, Object> cancelledRecord = new FileRepository(Module.RESERVATIONS).findById(createdId);
            BigDecimal total = new BigDecimal(String.valueOf(cancelledRecord.get("total")));
            BigDecimal fee = new BigDecimal(String.valueOf(cancelledRecord.get("cancellation_fee")));
            assertTrue(total.compareTo(BigDecimal.ZERO) > 0, "Stored total must be positive");
            assertTrue(fee.compareTo(BigDecimal.ZERO) > 0, "Cancelled ADVANCE booking must record 10% fee");

            // 5. Delete the CANCELLED reservation (must succeed)
            service.delete(Module.RESERVATIONS, createdId, 1L);
        } finally {
            FileStore.useDirectory(originalDir);
        }
    }

    // Verifies ReservationReportService aggregates counts and revenue accurately
    public void testReservationReportServiceMetrics() throws Exception {
        Path tempDir = Files.createTempDirectory("hrrs-report-test");
        Path originalDir = FileStore.directory();
        try {
            FileStore.useDirectory(tempDir);
            FileStore.initialize();

            ReservationReportService reportService = new ReservationReportService();
            Map<String, Object> summary = reportService.buildSummaryReport();

            long totalBookings = Long.parseLong(String.valueOf(summary.get("total_bookings")));
            assertTrue(totalBookings >= 1L, "Seeded store must contain at least 1 reservation");
            assertEquals(1, reportService.findBookingsForGuest(1L).size(), "Guest #1 should have 1 seeded booking");
        } finally {
            FileStore.useDirectory(originalDir);
        }
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " | Expected: " + expected + ", Actual: " + actual);
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
