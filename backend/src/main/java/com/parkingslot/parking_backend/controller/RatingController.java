
package com.parkingslot.parking_backend.controller;

import com.parkingslot.parking_backend.model.ParkingRating;
import com.parkingslot.parking_backend.repository.RatingRepository;
import com.parkingslot.parking_backend.repository.BookingRepository;
import com.parkingslot.parking_backend.repository.ParkingLotRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final ParkingLotRepository parkingLotRepository;

    public RatingController(
            RatingRepository ratingRepository,
            BookingRepository bookingRepository,
            ParkingLotRepository parkingLotRepository) {
        this.ratingRepository = ratingRepository;
        this.bookingRepository = bookingRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    @GetMapping("/lot/{parkingLotId}")
    public Map<String, Object> getRatingSummary(
            @PathVariable Long parkingLotId) {

        List<ParkingRating> ratings =
                ratingRepository.findByParkingLotId(parkingLotId);

        double average = ratings.stream()
                .mapToInt(ParkingRating::getRating)
                .average()
                .orElse(0.0);

        Map<String, Object> result = new HashMap<>();
        result.put("averageRating",
                Math.round(average * 10.0) / 10.0);
        result.put("ratingCount", ratings.size());

        return result;
    }

    @PostMapping
    public ResponseEntity<?> submitRating(
            @RequestParam UUID userId,
            @RequestParam Long parkingLotId,
            @RequestParam Integer rating) {

        if (rating == null || rating < 1 || rating > 5) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Rating must be between 1 and 5"
                    ));
        }

        if (!parkingLotRepository.existsById(parkingLotId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Parking lot not found"
                    ));
        }

        boolean eligible =
                bookingRepository.hasConfirmedBooking(
                        userId, parkingLotId);

        if (!eligible) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message",
                            "You need a confirmed booking to rate this parking lot"
                    ));
        }

        ParkingRating saved =
                ratingRepository
                        .findByUserIdAndParkingLotId(
                                userId, parkingLotId)
                        .orElseGet(ParkingRating::new);

        saved.setUserId(userId);
        saved.setParkingLotId(parkingLotId);
        saved.setRating(rating);
        saved.setCreatedAt(OffsetDateTime.now());

        ratingRepository.save(saved);

        return ResponseEntity.ok(Map.of(
                "message", "Rating saved successfully",
                "rating", saved.getRating()
        ));
    }
}
