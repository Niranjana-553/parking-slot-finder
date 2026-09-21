package com.parkingslot.parking_backend.controller;

import com.parkingslot.parking_backend.model.Profile;
import com.parkingslot.parking_backend.service.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public Profile getProfile(@PathVariable UUID userId) {
        return profileService.getProfile(userId);
    }

    @PutMapping("/{userId}")
    public Profile updateProfile(
            @PathVariable UUID userId,
            @RequestBody Profile profile) {
        return profileService.updateProfile(userId, profile);
    }
}