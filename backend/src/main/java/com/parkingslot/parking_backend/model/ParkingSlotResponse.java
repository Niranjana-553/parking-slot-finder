package com.parkingslot.parking_backend.model;

public class ParkingSlotResponse {

    private Long id;
    private String slotNumber;
    private String status;
    private java.math.BigDecimal price;

    public ParkingSlotResponse() {
    }

    public ParkingSlotResponse(
            Long id,
            String slotNumber,
            String status,
            java.math.BigDecimal price) {

        this.id = id;
        this.slotNumber = slotNumber;
        this.status = status;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public String getStatus() {
        return status;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }
}