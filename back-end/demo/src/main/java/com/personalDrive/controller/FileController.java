package com.personalDrive.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import com.personalDrive.apiErrorHandler.ApiErrors;
import com.personalDrive.model.FileDTOs.CreateFileReq;
import com.personalDrive.model.FileDTOs.FileInfo;
import com.personalDrive.model.FileDTOs.FileResponse;
import com.personalDrive.service.FileServiceFunc;
import com.personalDrive.service.FileServiceFunc.FileOpenResp;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/files")
public class FileController {

    private final FileServiceFunc fileServiceFunc;

    public FileController(FileServiceFunc fileServiceFunc, ApiErrors apiErrors) {
        this.fileServiceFunc = fileServiceFunc;
    }

    @PostMapping(path = "upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileResponse> postMethodName(@RequestBody CreateFileReq req) {
        FileResponse resp = fileServiceFunc.createFile(req);
        return ResponseEntity.created(URI.create("api/files" + resp.id())).body(resp);
    }

    @GetMapping("/{folderId}/list")
    public ResponseEntity<List<FileInfo>> listFilesInFolder(@PathVariable Long folderId, @RequestParam Long ownerId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        List<FileInfo> resp = fileServiceFunc.listFiles(folderId, ownerId, page, size, null);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable Long id, @RequestParam Long ownerId,
            @RequestParam(required = false, defaultValue = "inline") String disposition) {

        FileOpenResp resp = fileServiceFunc.openDownloadFile(id, ownerId);

        ContentDisposition contentDisposition = ContentDisposition.builder(disposition)
                .filename(resp.name(), StandardCharsets.UTF_8).build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType(resp.contentType()));
        header.setContentLength(resp.length());
        header.setContentDisposition(contentDisposition);
        header.setETag("\"sha256-" + resp.sha256() + "\"");
        header.set(HttpHeaders.ACCEPT_RANGES, "bytes");

        StreamingResponseBody body = os -> resp.pipe().accept(os);

        return ResponseEntity
                .ok()
                .headers(header)
                .body(body);
    }

}
