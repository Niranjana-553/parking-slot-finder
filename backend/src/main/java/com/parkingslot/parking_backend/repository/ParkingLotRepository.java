package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository
        extends JpaRepository<ParkingLot, Long> {
}