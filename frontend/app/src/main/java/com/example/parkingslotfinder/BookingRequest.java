package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("user_id")
    public String userId;
    @SerializedName("slot_id")
    public Integer slotId;
    @SerializedName("start_time")
    public String startTime;
    @SerializedName("end_time")
    public String endTime;

    public BookingRequest(String userId, Integer slotId, String startTime, String endTime) {
        this.userId = userId;
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
