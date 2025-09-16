package com.personalDrive.model;

public class FileDTOs {
    public record CreateFileReq(Long ownerId, Long folderId, Long blobId, String name) {
    }

    public record FileResponse(Long id, Long ownerId, Long blobId, String name) {
    }

    public record FileInfo(Long id, String name, Long folderId, Long blobSize, String blobContentType,
            java.time.Instant updatedAt) {
    }
}
