package com.example.parkingslotfinder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {
    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body SignupRequest request);

    @POST("auth/v1/token")
    Call<AuthResponse> login(
            @Query("grant_type") String grantType,
            @Body LoginRequest request);
}
