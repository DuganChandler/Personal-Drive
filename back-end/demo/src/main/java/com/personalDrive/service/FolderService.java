package com.personalDrive.service;

import com.personalDrive.model.FolderDTOs.CreateFolderReq;
import com.personalDrive.model.FolderDTOs.FolderResponse;
import com.personalDrive.model.FolderDTOs.GetFolderReq;

public interface FolderService {
    public FolderResponse createFolder(CreateFolderReq req);

    public FolderResponse getFolder(GetFolderReq req);
}
