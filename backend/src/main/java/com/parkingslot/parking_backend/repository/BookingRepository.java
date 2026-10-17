package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(UUID userId);

    List<Booking> findByParkingSlotId(Long parkingSlotId);

    List<Booking> findByParkingLotId(Long parkingLotId);

    boolean existsByParkingSlotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
        Long parkingSlotId,
        String status,
        OffsetDateTime endTime,
        OffsetDateTime startTime
);
    
@Query("""
    SELECT COUNT(b) > 0
    FROM Booking b
    WHERE b.userId = :userId
      AND b.parkingLotId = :parkingLotId
      AND LOWER(b.status) = 'confirmed'
""")
boolean hasConfirmedBooking(
        @Param("userId") UUID userId,
        @Param("parkingLotId") Long parkingLotId
);

}