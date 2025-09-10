package com.personalDrive.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.personalDrive.model.FolderDTOs.CreateFolderReq;
import com.personalDrive.model.FolderDTOs.FolderChild;
import com.personalDrive.model.FolderDTOs.FolderResponse;
import com.personalDrive.model.FolderDTOs.GetFolderReq;
import com.personalDrive.service.FolderServiceFunc;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("api/folders")
public class FolderController {
    private final FolderServiceFunc folderServiceFunc; 

    public FolderController(FolderServiceFunc folderServiceFunc) {
        this.folderServiceFunc = folderServiceFunc;
    }

    @PostMapping(path = "upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FolderResponse> createFolder(@RequestBody CreateFolderReq req) {
        FolderResponse resp = folderServiceFunc.createFolder(req);
        return ResponseEntity.created(URI.create("api/folders" + resp.id())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderResponse> getFolder(@PathVariable Long id, @RequestParam Long ownerId) {
        FolderResponse resp = folderServiceFunc.getFolder(new GetFolderReq(ownerId, id));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<FolderChild>> getChildren(@PathVariable Long id,
                                                         @RequestParam Long ownerId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "50") int size) {
        List<FolderChild> resp = folderServiceFunc.listChildren(ownerId, id, page, size);
        return ResponseEntity.ok(resp);
    }
    
}
