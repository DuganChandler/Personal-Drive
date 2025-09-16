package com.personalDrive.service;

import com.personalDrive.model.FileDTOs.CreateFileReq;
import com.personalDrive.model.FileDTOs.FileResponse;

public interface FileService {
    public FileResponse createFile(CreateFileReq req); 
}
