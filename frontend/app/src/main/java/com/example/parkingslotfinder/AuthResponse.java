package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token") public String accessToken;
    @SerializedName("refresh_token") public String refreshToken;
    public AuthUser user;
    public String msg;
    public String error_description;
}
