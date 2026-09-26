package com.example.parkingslotfinder;

public final class ApiConfig {
    private ApiConfig() {}

    // Android emulator -> Spring Boot running on the development machine.
    public static final String BASE_URL =
            "http://127.0.0.1:8080/";
}
