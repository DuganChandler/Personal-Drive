package com.personalDrive.service;

import org.springframework.stereotype.Service;

import com.personalDrive.apiErrorHandler.NotFoundException;
import com.personalDrive.model.Blob;
import com.personalDrive.model.File;
import com.personalDrive.model.Folder;
import com.personalDrive.model.User;
import com.personalDrive.model.FileDTOs.CreateFileReq;
import com.personalDrive.model.FileDTOs.FileResponse;
import com.personalDrive.repository.BlobRepository;
import com.personalDrive.repository.FileRepository;
import com.personalDrive.repository.FolderRepository;
import com.personalDrive.repository.UserRepository;

@Service
public class FileServiceFunc implements FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final BlobRepository blobRepository;

    public FileServiceFunc(FileRepository fileRepository, UserRepository userRepository, FolderRepository folderRepository, BlobRepository blobRepository) {
        this. blobRepository = blobRepository;
        this.fileRepository = fileRepository; 
        this.userRepository = userRepository;
        this. folderRepository = folderRepository;
    }

    @Override
    public FileResponse createFile(CreateFileReq req) {
        if (req.folderId() == null) {
            throw new IllegalArgumentException("Folder id is required");
        }

        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        if (req.ownerId() == null) {
            throw new IllegalArgumentException("Owner id is required");
        }

        if (req.blobId() == null) {
            throw new IllegalArgumentException("Blob id is required");
        }

        User owner = userRepository.findById(req.ownerId()).orElseThrow(() -> new NotFoundException("Owner not found", null));

        Folder folder = folderRepository.findByIdAndOwner_Id(req.folderId(), owner.getId()).orElseThrow(() -> new NotFoundException("Folder not found with given owner id", null));

        Blob blob = blobRepository.findById(req.blobId()).orElseThrow(() -> new NotFoundException("Blob with given id not found", null));

        File file= new File();
        file.setOwner(owner);
        file.setFolder(folder);
        file.setBlob(blob);
        file.setName(req.name().trim());

        File saved = fileRepository.saveAndFlush(file);

        return new FileResponse(
            saved.getId(),
            saved.getOwner().getId(),
            saved.getBlob().getId(),
            saved.getName()
        );
    }

}
