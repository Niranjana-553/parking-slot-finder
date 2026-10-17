package com.parkingslot.parking_backend.service;

import com.parkingslot.parking_backend.model.Booking;
import com.parkingslot.parking_backend.model.ParkingSlot;
import com.parkingslot.parking_backend.model.ParkingLot;
import com.parkingslot.parking_backend.repository.BookingRepository;
import com.parkingslot.parking_backend.repository.ParkingSlotRepository;
import com.parkingslot.parking_backend.repository.ParkingLotRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import com.parkingslot.parking_backend.model.Notification;
import com.parkingslot.parking_backend.repository.NotificationRepository;
import java.math.BigDecimal;
import java.time.Duration;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final NotificationRepository notificationRepository;

    public BookingService(
            BookingRepository bookingRepository,
            ParkingSlotRepository parkingSlotRepository,
            ParkingLotRepository parkingLotRepository,
            NotificationRepository notificationRepository) {

        this.bookingRepository = bookingRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.notificationRepository = notificationRepository;
    }

    // Create a new booking
    @Transactional
    public Booking createBooking(
            UUID userId,
            Long parkingSlotId,
            Long parkingLotId,
            OffsetDateTime startTime,
            OffsetDateTime endTime) {

        // 1. Check that end time is after start time
        // 1. Validate booking times
if (startTime == null || endTime == null) {
    throw new RuntimeException("Start time and end time are required");
}

if (!endTime.isAfter(startTime)) {
    throw new RuntimeException("End time must be after start time");
}

if (!startTime.isAfter(OffsetDateTime.now())) {
    throw new RuntimeException("Start time must be in the future");
}

        // 2. Find the parking slot
        ParkingSlot slot = parkingSlotRepository.findById(parkingSlotId)
                .orElseThrow(() ->
                        new RuntimeException("Parking slot not found"));

        // 3. Check that the slot belongs to the selected parking lot
        if (!slot.getParkingLotId().equals(parkingLotId)) {
            throw new RuntimeException(
                    "Parking slot does not belong to this parking lot"
            );
        }

        // Check for overlapping confirmed bookings
boolean overlaps =
        bookingRepository
                .existsByParkingSlotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        parkingSlotId,
                        "confirmed",
                        endTime,
                        startTime
                );

if (overlaps) {
    throw new RuntimeException(
            "This slot is already booked for the selected time."
    );
}

      
        
        // Calculate total booking cost
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() ->
                        new RuntimeException("Parking lot not found"));

        BigDecimal hourlyPrice = parkingLot.getPrice();

        if (hourlyPrice == null || hourlyPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Invalid parking price");
        }

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();

        if (Duration.between(startTime, endTime).getSeconds() % 60 != 0) {
                durationMinutes++;
        }

        BigDecimal durationHours = BigDecimal.valueOf(durationMinutes)
                .divide(BigDecimal.valueOf(60), 4, java.math.RoundingMode.UP);

        BigDecimal totalCost = hourlyPrice.multiply(durationHours)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        // 5. Create booking
        Booking booking = new Booking();

        booking.setUserId(userId);
        booking.setParkingSlotId(parkingSlotId);
        booking.setParkingLotId(parkingLotId);
        booking.setBookingTime(OffsetDateTime.now());
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setTotalCost(totalCost.doubleValue());
        booking.setStatus("confirmed");
        booking.setCreatedAt(OffsetDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        // Create booking notification
Notification notification = new Notification();

notification.setUserId(userId);
notification.setTitle("Booking Confirmed");
notification.setMessage(
        "Your parking slot " + slot.getSlotNumber()
        + " has been booked successfully."
);
notification.setRead(false);
notification.setCreatedAt(OffsetDateTime.now());

notificationRepository.save(notification);

        return savedBooking;
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Get bookings of a user
    public List<Booking> getUserBookings(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Get bookings for a parking slot
    public List<Booking> getSlotBookings(Long parkingSlotId) {
        return bookingRepository.findByParkingSlotId(parkingSlotId);
    }

// Cancel a booking
@Transactional
public Booking cancelBooking(Long bookingId) {

    // 1. Find the booking
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() ->
                    new RuntimeException("Booking not found"));

    // 2. Check if already cancelled
    if ("cancelled".equalsIgnoreCase(booking.getStatus())) {
        throw new RuntimeException("Booking is already cancelled");
    }

    // 3. Find the parking slot
    ParkingSlot slot = parkingSlotRepository.findById(
            booking.getParkingSlotId()
    ).orElseThrow(() ->
            new RuntimeException("Parking slot not found"));

 
    

    // 6. Update booking status
    booking.setStatus("cancelled");
    // Create cancellation notification
Notification notification = new Notification();

notification.setUserId(booking.getUserId());
notification.setTitle("Booking Cancelled");
notification.setMessage(
        "Your booking for parking slot "
        + slot.getSlotNumber()
        + " has been cancelled."
);
notification.setRead(false);
notification.setCreatedAt(OffsetDateTime.now());

notificationRepository.save(notification);
    return bookingRepository.save(booking);
}

public boolean hasOverlappingBooking(
        Long parkingSlotId,
        OffsetDateTime startTime,
        OffsetDateTime endTime) {

    return bookingRepository
            .existsByParkingSlotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                    parkingSlotId, "confirmed", endTime, startTime);
}

}