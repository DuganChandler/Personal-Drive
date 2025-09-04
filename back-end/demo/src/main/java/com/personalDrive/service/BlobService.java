package com.personalDrive.service;

import java.io.InputStream;

import com.personalDrive.model.Blob;
import com.personalDrive.model.BlobDTOs.BlobDTO;
import com.personalDrive.model.BlobDTOs.BlobResponse;

public interface BlobService {
    public BlobDTO store(InputStream in, String contentType) throws Exception; 
    public BlobResponse uploadAndRegister(InputStream in, String contententType);
    public Blob upsertBlobRow(BlobDTO response);
 }
    
