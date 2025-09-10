package com.personalDrive.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalDrive.apiErrorHandler.NotFoundException;
import com.personalDrive.model.Folder;
import com.personalDrive.model.User;
import com.personalDrive.model.FolderDTOs.BreadCrumb;
import com.personalDrive.model.FolderDTOs.CreateFolderReq;
import com.personalDrive.model.FolderDTOs.FolderChild;
import com.personalDrive.model.FolderDTOs.FolderResponse;
import com.personalDrive.model.FolderDTOs.GetFolderReq;
import com.personalDrive.repository.FolderRepository;
import com.personalDrive.repository.UserRepository;

@Service
public class FolderServiceFunc implements FolderService {
    private final FolderRepository fRepository;
    private final UserRepository uRepository;

    public FolderServiceFunc(FolderRepository folderRepository, UserRepository uRepository) {
        fRepository = folderRepository;
        this.uRepository = uRepository;
    }

    @Override
    @Transactional
    public FolderResponse createFolder(CreateFolderReq req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("Folder name is required.");
        }

        User owner = uRepository.findById(req.ownerId())
                .orElseThrow(() -> new NotFoundException("Owner not found", null));

        Folder parent = null;
        if (req.parentId() != null) {
            parent = fRepository.findById(req.parentId())
                    .orElseThrow(() -> new NotFoundException("Parent not found", null));
        }

        Folder folder = new Folder();
        folder.setOwner(owner);
        folder.setParent(parent);
        folder.setName(req.name().trim());

        Folder saved = fRepository.saveAndFlush(folder);

        return new FolderResponse(
                saved.getId(),
                saved.getOwner().getId(),
                saved.getName(),
                saved.getParent() != null ? folder.getParent().getId() : null);
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponse getFolder(GetFolderReq req) {
        if (req.folderId() == null) {
            throw new IllegalArgumentException("Folder ID is required.");
        }

        if (req.ownerId() == null) {
            throw new IllegalArgumentException("Owner ID is required.");
        }

        Folder folder = fRepository.findByIdAndOwner_Id(req.folderId(), req.ownerId())
                .orElseThrow(() -> new NotFoundException("Folder does not exist", null));

        return new FolderResponse(
                folder.getId(),
                folder.getOwner().getId(),
                folder.getName(),
                folder.getParent() != null ? folder.getParent().getId() : null);
    }

    @Transactional(readOnly = true)
    public List<FolderChild> listChildren(Long ownerId, Long parentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fRepository.findByOwner_IdAndParent_Id(ownerId, parentId, pageable)
                .map(folder -> new FolderChild(folder.getId(), parentId, folder.getName()))
                .getContent();
    }

    @Transactional(readOnly = true)
    public List<BreadCrumb> listBreadCrumbs(Long folderId, Long ownerId) {
        return fRepository.findBreadCrumbs(folderId, ownerId)
                .orElseThrow(() -> new NotFoundException("Error getting bread crumbs", null));
    }

}
