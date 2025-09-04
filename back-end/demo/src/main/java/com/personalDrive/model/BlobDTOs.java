package com.personalDrive.model;

public class BlobDTOs {
    public record BlobResponse (Long id, String sha256, Long size, String contentType, String storagePath) {}
    public record BlobDTO (String sha256, Long size, String contentType, String storagePath) {}
}
