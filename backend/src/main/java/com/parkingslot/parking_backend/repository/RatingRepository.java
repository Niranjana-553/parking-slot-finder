
package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.ParkingRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository
        extends JpaRepository<ParkingRating, Long> {

    List<ParkingRating> findByParkingLotId(Long parkingLotId);

    Optional<ParkingRating> findByUserIdAndParkingLotId(
            UUID userId, Long parkingLotId
    );
}
