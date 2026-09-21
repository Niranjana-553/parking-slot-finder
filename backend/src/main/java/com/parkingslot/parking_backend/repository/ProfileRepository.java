package com.parkingslot.parking_backend.repository;

import com.parkingslot.parking_backend.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}