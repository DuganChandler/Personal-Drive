package com.personalDrive.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.personalDrive.model.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByIdAndOwner_Id(Long folderId, Long ownerId);

    Page<Folder> findByOwner_IdAndParent_Id(Long ownerId, Long parentId, Pageable pageable);
}
