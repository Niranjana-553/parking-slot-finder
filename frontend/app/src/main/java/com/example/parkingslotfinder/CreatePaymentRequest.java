
package com.example.parkingslotfinder;

public class CreatePaymentRequest {
    public String userId;
    public Long parkingSlotId;
    public Long parkingLotId;
    public String startTime;
    public String endTime;

    public CreatePaymentRequest(
            String userId,
            Long parkingSlotId,
            Long parkingLotId,
            String startTime,
            String endTime) {
        this.userId = userId;
        this.parkingSlotId = parkingSlotId;
        this.parkingLotId = parkingLotId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
