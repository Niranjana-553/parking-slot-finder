package com.parkingslot.parking_backend.controller;

import com.parkingslot.parking_backend.model.ParkingLot;
import com.parkingslot.parking_backend.repository.ParkingLotRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotController(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    // Get all parking lots
    @GetMapping
    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    // Get one parking lot by ID
    @GetMapping("/{id}")
    public ParkingLot getParkingLot(@PathVariable Long id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));
    }
}