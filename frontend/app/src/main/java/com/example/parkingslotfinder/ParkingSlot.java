package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;

public class ParkingSlot {

    public Integer id;

    @SerializedName("slot_number")
    public String slotNumber;

    public String status;

    public Double price;
}