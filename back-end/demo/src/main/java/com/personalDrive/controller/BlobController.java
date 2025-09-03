package com.personalDrive.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.personalDrive.service.DiskBlobStoreService;

import java.io.InputStream;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/blobs")
public class BlobController {
    private final DiskBlobStoreService diskBlobStoreService;
    
    public BlobController(DiskBlobStoreService diskBlobStoreService) {
        this.diskBlobStoreService = diskBlobStoreService;
    }
    
    @PostMapping(path = "/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BlobResponse uploadMultipart(@RequestParam("file") MultipartFile part) throws Exception {
        try (InputStream in = part.getInputStream()) {
            // return diskBlobStoreService.store(in, part.getContentType()).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.internalServerError().build());
            DiskBlobStoreService.BlobDTO stored = diskBlobStoreService.store(in, part.getContentType());
            return new BlobResponse(stored.sha256(), stored.size(), stored.contentType(), stored.storagePath());
        }
    }

  public record BlobResponse(String sha256, long sizeBytes, String contentType, String storagePath) {}
}
