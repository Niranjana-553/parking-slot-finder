
package com.parkingslot.parking_backend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreatePaymentRequest {
    public UUID userId;
    public Long parkingSlotId;
    public Long parkingLotId;
    public OffsetDateTime startTime;
    public OffsetDateTime endTime;
}
