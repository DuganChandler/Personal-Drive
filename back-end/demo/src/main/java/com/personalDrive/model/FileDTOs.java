package com.personalDrive.model;

public class FileDTOs {
    public record CreateFileReq(Long ownerId, Long folderId, Long blobId, String name) {
    }

    public record FileResponse(Long id, Long ownerId, Long blobId, String name) {
    }
}
