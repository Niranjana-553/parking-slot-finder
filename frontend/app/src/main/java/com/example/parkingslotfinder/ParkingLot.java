package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;

public class ParkingLot {

    public Integer id;

    public String name;

    public String address;
    public String district;
    public Double latitude;
    public Double longitude;


    @SerializedName(
            value = "availableSlots",
            alternate = {"available_slots"}
    )
    public Integer availableSlots;

    // =============================================================
    // DISPLAY LOCATION
    // =============================================================

    public String displayLocation() {

        if (address != null
                && !address.trim().isEmpty()) {

            return address;
        }

        return "Location unavailable";
    }
}