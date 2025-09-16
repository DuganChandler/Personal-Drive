package com.personalDrive.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.personalDrive.model.File;
import com.personalDrive.model.FileDTOs.FileInfo;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByIdAndOwner_Id(Long fileId, Long ownerId);

    @Query("""
            SELECT NEW com.personalDrive.model.FileDTOs$FileInfo(
                f.id,
                f.name,
                f.folder.id,
                b.sizeBytes,
                b.contentType,
                b.createdAt
            )
            FROM File f
            JOIN f.owner o
            JOIN f.blob b
            WHERE o.id = :ownerId
                AND f.folder.id = :folderId
                """)
    Page<FileInfo> listFilesWithBlobInfo(Long folderId, Long ownerId, Pageable pageable);

}
