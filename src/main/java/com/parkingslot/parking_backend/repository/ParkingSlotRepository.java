package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.ParkingSlot;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

}