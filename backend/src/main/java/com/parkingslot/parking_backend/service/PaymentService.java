
package com.parkingslot.parking_backend.service;

import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.Utils;
import com.razorpay.RazorpayException;

import com.parkingslot.parking_backend.dto.CreatePaymentRequest;
import com.parkingslot.parking_backend.dto.VerifyPaymentRequest;
import com.parkingslot.parking_backend.model.PendingPayment;
import com.parkingslot.parking_backend.model.ParkingLot;
import com.parkingslot.parking_backend.model.ParkingSlot;
import com.parkingslot.parking_backend.repository.PendingPaymentRepository;
import com.parkingslot.parking_backend.repository.ParkingLotRepository;
import com.parkingslot.parking_backend.repository.ParkingSlotRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PendingPaymentRepository pendingRepository;
    private final ParkingSlotRepository slotRepository;
    private final ParkingLotRepository lotRepository;
    private final BookingService bookingService;
    private final String keySecret;
    private final String keyId;

    public PaymentService(
            RazorpayClient razorpayClient,
            PendingPaymentRepository pendingRepository,
            ParkingSlotRepository slotRepository,
            ParkingLotRepository lotRepository,
            BookingService bookingService,
            
@Value("${razorpay.key.secret}") String keySecret,
@Value("${razorpay.key.id}") String keyId) { 

        this.razorpayClient = razorpayClient;
        this.pendingRepository = pendingRepository;
        this.slotRepository = slotRepository;
        this.lotRepository = lotRepository;
        this.bookingService = bookingService;
        this.keySecret = keySecret;
        this.keyId = keyId;
    }

    public JSONObject createOrder(CreatePaymentRequest request)
            throws Exception {

        validateRequest(request);

        ParkingSlot slot = slotRepository.findById(request.parkingSlotId)
                .orElseThrow(() -> new RuntimeException("Parking slot not found"));

        if (!slot.getParkingLotId().equals(request.parkingLotId)) {
            throw new RuntimeException("Slot does not belong to this parking lot");
        }

        boolean overlaps = bookingService.hasOverlappingBooking(
                request.parkingSlotId, request.startTime, request.endTime);

        if (overlaps) {
            throw new RuntimeException("This slot is already booked for that time.");
        }

        ParkingLot lot = lotRepository.findById(request.parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        BigDecimal hourlyPrice = lot.getPrice();
        if (hourlyPrice == null || hourlyPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Invalid parking price");
        }

        long minutes = Duration.between(request.startTime, request.endTime).toMinutes();
        if (Duration.between(request.startTime, request.endTime).getSeconds() % 60 != 0) {
            minutes++;
        }

        BigDecimal hours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 4, RoundingMode.UP);

        BigDecimal total = hourlyPrice.multiply(hours)
                .setScale(2, RoundingMode.HALF_UP);

        // Razorpay expects the amount in the smallest currency unit (paise for INR).
        int amountPaise = total.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();

        if (amountPaise <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "park_" + UUID.randomUUID());

        Order order = razorpayClient.orders.create(orderRequest);
        String orderId = order.get("id");

        PendingPayment pending = new PendingPayment();
        pending.setUserId(request.userId);
        pending.setParkingSlotId(request.parkingSlotId);
        pending.setParkingLotId(request.parkingLotId);
        pending.setStartTime(request.startTime);
        pending.setEndTime(request.endTime);
        pending.setTotalCost(total);
        pending.setRazorpayOrderId(orderId);
        pending.setStatus("pending");
        pending.setCreatedAt(OffsetDateTime.now());

        pendingRepository.save(pending);

        JSONObject response = new JSONObject();
        response.put("orderId", orderId);
        response.put("amount", amountPaise);
        response.put("currency", "INR");
        response.put("keyId", keyId);
        return response;
    }

    @Transactional
    public Object verifyPayment(VerifyPaymentRequest request) throws Exception {

        if (request == null
                || request.razorpayOrderId == null
                || request.razorpayPaymentId == null
                || request.razorpaySignature == null) {
            throw new RuntimeException("Payment verification details are missing");
        }

        PendingPayment pending = pendingRepository
                .findByRazorpayOrderId(request.razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment order not found"));

        if ("paid".equalsIgnoreCase(pending.getStatus())) {
            throw new RuntimeException("This payment has already been processed");
        }

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", request.razorpayOrderId);
        attributes.put("razorpay_payment_id", request.razorpayPaymentId);
        attributes.put("razorpay_signature", request.razorpaySignature);

        Utils.verifyPaymentSignature(attributes, keySecret);

        // Rechecks times and overlap before confirming, then creates your
        // existing booking and confirmation notification.
        var booking = bookingService.createBooking(
                pending.getUserId(),
                pending.getParkingSlotId(),
                pending.getParkingLotId(),
                pending.getStartTime(),
                pending.getEndTime()
        );

        pending.setStatus("paid");
        pendingRepository.save(pending);

        return booking;
    }

    private void validateRequest(CreatePaymentRequest r) {
        if (r == null || r.userId == null || r.parkingSlotId == null
                || r.parkingLotId == null || r.startTime == null || r.endTime == null) {
            throw new RuntimeException("All booking details are required");
        }

        if (!r.endTime.isAfter(r.startTime)) {
            throw new RuntimeException("End time must be after start time");
        }

        if (!r.startTime.isAfter(OffsetDateTime.now())) {
            throw new RuntimeException("Start time must be in the future");
        }
    }
}
