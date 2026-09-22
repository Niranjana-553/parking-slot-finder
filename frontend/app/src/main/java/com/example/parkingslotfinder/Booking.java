package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;

public class Booking {

    public Integer id;

    @SerializedName("userId")
    public String userId;

    @SerializedName("parkingSlotId")
    public Integer slotId;

    @SerializedName("parkingLotId")
    public Integer parkingLotId;

    @SerializedName("startTime")
    public String startTime;

    @SerializedName("endTime")
    public String endTime;

    @SerializedName("status")
    public String status;

    public Double totalCost;
}