package com.parkingslot.parking_backend.service;

import com.parkingslot.parking_backend.model.Profile;
import com.parkingslot.parking_backend.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getProfile(UUID userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile updateProfile(UUID userId, Profile updatedProfile) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setName(updatedProfile.getName());
        profile.setPhone(updatedProfile.getPhone());

        return profileRepository.save(profile);
    }
}