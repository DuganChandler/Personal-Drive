package com.personalDrive.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personalDrive.model.BlobDTOs.BlobResponse;
import com.personalDrive.model.FileDTOs.CreateFileReq;
import com.personalDrive.model.FileDTOs.FileResponse;
import com.personalDrive.service.BlobUploadService;
import com.personalDrive.service.FileServiceFunc;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/files")
public class FileController {
    private final FileServiceFunc fileServiceFunc; 

    public FileController(FileServiceFunc fileServiceFunc) {
        this.fileServiceFunc = fileServiceFunc;
    }

    @PostMapping(path = "upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponse> postMethodName(@RequestBody CreateFileReq req) {
        FileResponse resp = fileServiceFunc.createFile(req);
        return ResponseEntity.created(URI.create("api/files" + resp.id())).body(resp);
    }

}
