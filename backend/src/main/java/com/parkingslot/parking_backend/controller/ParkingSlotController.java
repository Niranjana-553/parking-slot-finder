package com.parkingslot.parking_backend.controller;
import com.parkingslot.parking_backend.model.ParkingLot;
import com.parkingslot.parking_backend.model.ParkingSlotResponse;
import com.parkingslot.parking_backend.model.ParkingSlot;
import com.parkingslot.parking_backend.repository.ParkingLotRepository;
import com.parkingslot.parking_backend.repository.ParkingSlotRepository;
import com.parkingslot.parking_backend.repository.ParkingLotRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-slots")
public class ParkingSlotController {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public ParkingSlotController(
        ParkingSlotRepository parkingSlotRepository,
        ParkingLotRepository parkingLotRepository) {

    this.parkingSlotRepository = parkingSlotRepository;
    this.parkingLotRepository = parkingLotRepository;
}

    // Get all parking slots
    @GetMapping
    public List<ParkingSlot> getAllParkingSlots() {
        return parkingSlotRepository.findAll();
    }
    // Get all available parking slots
    @GetMapping("/available")
    public List<ParkingSlot> getAvailableParkingSlots() {
    return parkingSlotRepository.findByOccupiedFalse();
}
// Get available parking slots for a specific parking lot
@GetMapping("/available/lot/{parkingLotId}")
public List<ParkingSlotResponse> getAvailableSlotsByParkingLot(
        @PathVariable Long parkingLotId) {

    ParkingLot parkingLot =
            parkingLotRepository.findById(parkingLotId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Parking lot not found"
                            ));

    List<ParkingSlot> slots =
        parkingSlotRepository.findByParkingLotId(parkingLotId);

    return slots.stream()
            .map(slot -> new ParkingSlotResponse(
                    slot.getId(),
                    slot.getSlotNumber(),
                    slot.getStatus(),
                    parkingLot.getPrice()
            ))
            .toList();
}

    // Get one parking slot by ID
    @GetMapping("/{id}")
    public ParkingSlot getParkingSlot(@PathVariable Long id) {
        return parkingSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking slot not found"));
    }

    // Add a new parking slot
    @PostMapping
    public ParkingSlot addParkingSlot(@RequestBody ParkingSlot parkingSlot) {
        return parkingSlotRepository.save(parkingSlot);
    }

    // Update parking slot
    @PutMapping("/{id}")
    public ParkingSlot updateParkingSlot(
            @PathVariable Long id,
            @RequestBody ParkingSlot updatedSlot) {

        ParkingSlot slot = parkingSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking slot not found"));

        slot.setSlotNumber(updatedSlot.getSlotNumber());
        slot.setOccupied(updatedSlot.getOccupied());
        slot.setParkingLotId(updatedSlot.getParkingLotId());
        slot.setSlotType(updatedSlot.getSlotType());
        slot.setStatus(updatedSlot.getStatus());

        return parkingSlotRepository.save(slot);
    }

    // Delete parking slot
    @DeleteMapping("/{id}")
    public String deleteParkingSlot(@PathVariable Long id) {

        parkingSlotRepository.deleteById(id);

        return "Parking slot deleted successfully";
    }
}