package com.parkingslot.parking_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.HttpURLConnection;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @GetMapping("/district")
public String getDistrict(
        @RequestParam double latitude,
        @RequestParam double longitude
) {

    try {

        String urlString =
                "https://nominatim.openstreetmap.org/reverse"
                        + "?lat=" + latitude
                        + "&lon=" + longitude
                        + "&format=json";

        URI uri = URI.create(urlString);

        HttpURLConnection connection =
                (HttpURLConnection) uri.toURL().openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty(
                "User-Agent",
                "ParkNow/1.0"
        );

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );

        StringBuilder response = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        // Parse the JSON response
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(response.toString());   

        String district =
                json.path("address")
                        .path("state_district")
                        .asText(null);

        if (district == null) {
            return "{\"error\":\"District not found\"}";
        }

        return "{\"district\":\"" + district + "\"}";

    } catch (Exception e) {

        return "{\"error\":\"Unable to determine district\"}";
    }
}
}
