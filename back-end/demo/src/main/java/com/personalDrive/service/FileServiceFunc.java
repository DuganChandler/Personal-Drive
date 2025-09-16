package com.personalDrive.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalDrive.apiErrorHandler.NotFoundException;
import com.personalDrive.apiErrorHandler.StorageException;
import com.personalDrive.model.Blob;
import com.personalDrive.model.File;
import com.personalDrive.model.Folder;
import com.personalDrive.model.User;
import com.personalDrive.model.FileDTOs.CreateFileReq;
import com.personalDrive.model.FileDTOs.FileInfo;
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
    private final BlobServiceFunc blobServiceFunc;

    public FileServiceFunc(FileRepository fileRepository, UserRepository userRepository,
            FolderRepository folderRepository, BlobRepository blobRepository, BlobServiceFunc blobServiceFunc) {
        this.blobRepository = blobRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.blobServiceFunc = blobServiceFunc;
    }

    public record FileOpenResp(
            String contentType, Long length, String sha256, String name,
            java.util.function.Consumer<java.io.OutputStream> pipe) {
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

        User owner = userRepository.findById(req.ownerId())
                .orElseThrow(() -> new NotFoundException("Owner not found", null));

        Folder folder = folderRepository.findByIdAndOwner_Id(req.folderId(), owner.getId())
                .orElseThrow(() -> new NotFoundException("Folder not found with given owner id", null));

        Blob blob = blobRepository.findById(req.blobId())
                .orElseThrow(() -> new NotFoundException("Blob with given id not found", null));

        File file = new File();
        file.setOwner(owner);
        file.setFolder(folder);
        file.setBlob(blob);
        file.setName(req.name().trim());

        File saved = fileRepository.saveAndFlush(file);

        return new FileResponse(
                saved.getId(),
                saved.getOwner().getId(),
                saved.getBlob().getId(),
                saved.getName());
    }

    @Transactional(readOnly = true)
    public List<FileInfo> listFiles(Long folderId, Long ownerId, int page, int size, String sort) {
        if (folderId == null) {
            throw new IllegalArgumentException("FolderId is requierd");
        }

        if (ownerId == null) {
            throw new IllegalArgumentException("OwnerId is requierd");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found", null));

        Folder folder = folderRepository.findByIdAndOwner_Id(folderId, owner.getId())
                .orElseThrow(() -> new NotFoundException("Folder not found with given owner id", null));

        Pageable pageable = PageRequest.of(page, size,
                sort != null ? Sort.by(sort).ascending() : Sort.by("name").ascending());
        return fileRepository.listFilesWithBlobInfo(folder.getId(), owner.getId(), pageable)
                .map(fileInfo -> new FileInfo(fileInfo.id(), fileInfo.name(), fileInfo.folderId(), fileInfo.blobSize(),
                        fileInfo.blobContentType(), fileInfo.updatedAt()))
                .getContent();
    }

    public FileOpenResp openDownloadFile(Long fileId, Long ownerId) {
        File file = fileRepository.findByIdAndOwner_Id(fileId, ownerId)
                .orElseThrow(() -> new NotFoundException("file not found with given fileId and ownerId", null));

        Blob blob = blobRepository.findById(file.getBlob().getId())
                .orElseThrow(() -> new NotFoundException("Blob not found from given file", null));

        String storagePath = blob.getStoragePath();
        String contentType = blob.getContentType();
        Long length = blob.getSize();
        String sha256 = blob.getSha256();
        String name = file.getName();

        return new FileOpenResp(contentType, length, sha256, name,
                out -> blobServiceFunc.open(storagePath, in -> {
                    byte[] buffer = new byte[1024 * 1024];
                    try {
                        for (int n; (n = in.read(buffer)) != -1;) {
                            out.write(buffer, 0, n);
                        }

                        out.flush();
                    } catch (IOException e) {
                        throw new StorageException("Failed to read blob", e);
                    }
                }));
    }
}
