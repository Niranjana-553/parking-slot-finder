package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(UUID userId);

    List<Booking> findByParkingSlotId(Long parkingSlotId);

    List<Booking> findByParkingLotId(Long parkingLotId);
}