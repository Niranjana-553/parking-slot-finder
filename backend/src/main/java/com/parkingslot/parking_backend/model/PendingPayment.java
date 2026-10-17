
package com.parkingslot.parking_backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pending_payments")
public class PendingPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "parking_slot_id", nullable = false)
    private Long parkingSlotId;

    @Column(name = "parking_lot_id", nullable = false)
    private Long parkingLotId;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "razorpay_order_id", nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(nullable = false)
    private String status = "pending";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public PendingPayment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Long getParkingSlotId() { return parkingSlotId; }
    public void setParkingSlotId(Long id) { this.parkingSlotId = id; }

    public Long getParkingLotId() { return parkingLotId; }
    public void setParkingLotId(Long id) { this.parkingLotId = id; }

    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime time) { this.startTime = time; }

    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime time) { this.endTime = time; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal cost) { this.totalCost = cost; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String id) { this.razorpayOrderId = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime time) { this.createdAt = time; }
}
