package com.personalDrive.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.personalDrive.model.BlobDTOs.BlobResponse;
import com.personalDrive.service.BlobServiceFunc;

import java.io.InputStream;
import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/upload")
public class BlobController {
    private final BlobServiceFunc blobService;
    
    public BlobController(BlobServiceFunc blobService) {
        this.blobService = blobService;
    }
    
    @PostMapping(path = "blob/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlobResponse> uploadMultipart(@RequestParam("file") MultipartFile file) throws Exception {
        try (InputStream in = file.getInputStream()) {
            BlobResponse response = blobService.uploadAndRegister(in, file.getContentType());
            return ResponseEntity.created(URI.create("api/blobs" + response.id())).body(response);
        }
    }
}
