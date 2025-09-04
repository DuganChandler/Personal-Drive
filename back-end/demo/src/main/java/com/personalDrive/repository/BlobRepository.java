package com.personalDrive.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personalDrive.model.Blob;

public interface BlobRepository extends JpaRepository<Blob, Long> {
    Optional<Blob> findBySha256(String sha256);
}
