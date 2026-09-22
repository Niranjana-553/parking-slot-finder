package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;

public class ParkingLot {

    public Integer id;

    public String name;

    public String address;

    /*
     * Accept BOTH possible backend names:
     *
     * availableSlots
     * available_slots
     *
     * This prevents the value from becoming null/0
     * because of a JSON naming mismatch.
     */
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