package com.personalDrive.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.personalDrive.model.Folder;
import com.personalDrive.model.FolderDTOs.BreadCrumb;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByIdAndOwner_Id(Long folderId, Long ownerId);

    Page<Folder> findByOwner_IdAndParent_Id(Long ownerId, Long parentId, Pageable pageable);

    @Query( value = """
        WITH RECURSIVE 
        bc AS 
        (
            SELECT f.id, f.parent_id, f.name, 1 AS depth
            FROM folder f
            WHERE f.id = :folderId AND f.owner_id = :ownerId

            UNION ALL

            SELECT pf.id, pf.parent_id, pf.name, bc.depth + 1
            FROM folder pf
            JOIN bc ON pf.id = bc.parent_id
            WHERE pf.owner_id = :ownerId
        ) 
        SELECT id, name
        FROM bc
        ORDER BY depth DESC;
    """, nativeQuery = true)
    Optional<List<BreadCrumb>> findBreadCrumbs(Long folderId, Long ownerId);
}
