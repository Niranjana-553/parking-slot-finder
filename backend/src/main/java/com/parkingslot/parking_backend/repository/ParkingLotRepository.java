package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.ParkingLot;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

}