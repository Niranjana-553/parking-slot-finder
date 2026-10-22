package com.parkingslot.parking_backend.controller;

import com.parkingslot.parking_backend.model.Booking;
import com.parkingslot.parking_backend.service.BookingService;

import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Create booking
    @PostMapping
    public Booking createBooking(
            @RequestParam UUID userId,
            @RequestParam Long parkingSlotId,
            @RequestParam Long parkingLotId,
            @RequestParam OffsetDateTime startTime,
            @RequestParam OffsetDateTime endTime) {

        return bookingService.createBooking(
                userId,
                parkingSlotId,
                parkingLotId,
                startTime,
                endTime
        );
    }

    // Get all bookings
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Get bookings for a user
    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable UUID userId) {
        return bookingService.getUserBookings(userId);
    }

    // Get bookings for a parking slot
    @GetMapping("/slot/{parkingSlotId}")
    public List<Booking> getSlotBookings(
            @PathVariable Long parkingSlotId) {

        return bookingService.getSlotBookings(parkingSlotId);
    }

    // Cancel booking
    @PutMapping("/{bookingId}/cancel")
    public Booking cancelBooking(@PathVariable Long bookingId) {
    return bookingService.cancelBooking(bookingId);
}

@GetMapping("/availability")
public Map<String, Boolean> checkAvailability(
        @RequestParam Long parkingSlotId,
        @RequestParam OffsetDateTime startTime,
        @RequestParam OffsetDateTime endTime) {

    if (!endTime.isAfter(startTime)) {
        throw new IllegalArgumentException(
                "End time must be after start time");
    }

    boolean available = !bookingService.hasOverlappingBooking(
            parkingSlotId,
            startTime,
            endTime
    );

    return Map.of("available", available);
}
}