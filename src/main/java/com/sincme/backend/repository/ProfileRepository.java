package com.sincme.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sincme.backend.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<Profile> findByUser_Id(Long userId);
}