package com.personalDrive.model;

public class FolderDTOs {
    public record FolderResponse(Long id, Long ownerId, String name, Long parentId) {} 
    public record CreateFolderReq(Long ownerId, Long parentId, String name) {}
    public record GetFolderReq(Long ownerId, Long folderId) {}
    public record FolderChild(Long id, Long parentId, String name) {}
}
