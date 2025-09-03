package com.personalDrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personalDrive.model.Blob;

public interface BlobRepository extends JpaRepository<Blob, Long> { }
