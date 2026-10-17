
package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByOccupiedFalse();

    List<ParkingSlot> findByParkingLotIdAndOccupiedFalse(Long parkingLotId);

    List<ParkingSlot> findByParkingLotId(Long parkingLotId);
}
