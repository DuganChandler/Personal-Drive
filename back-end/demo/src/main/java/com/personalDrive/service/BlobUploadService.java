package com.personalDrive.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalDrive.apiErrorHandler.StorageException;
import com.personalDrive.model.Blob;
import com.personalDrive.model.BlobDTOs.BlobDTO;
import com.personalDrive.model.BlobDTOs.BlobResponse;
import com.personalDrive.repository.BlobRepository;


@Service
public class BlobUploadService implements BlobService {
    private final Path root = Path.of(System.getProperty("APP_STORAGE_ROOT", "/drive-data/blobs"));
    private final BlobRepository blobRepository;

    public BlobUploadService(BlobRepository blobRepository) {
        this.blobRepository = blobRepository;
    }

    @Override
    public BlobResponse uploadAndRegister(InputStream in, String contententType) {
        BlobDTO response;

        try {
            response = store(in, contententType);
        } catch (Exception e) {
            throw new StorageException("Failed to write blob to disk.", e); 
        }
        Blob blob = upsertBlobRow(response); 
        return new BlobResponse(blob.getId(), blob.getSha256(), blob.getSize(), blob.getContentType(), blob.getStoragePath());
    }

    @Override
    public BlobDTO store(InputStream in, String contentType) throws Exception {
        Files.createDirectories(root.resolve("tmp"));

        // write to a temp file while hashing
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Path tmp = Files.createTempFile(root.resolve("tmp"), "up-", ".part");
        long size = 0;

        try (FileOutputStream fileOutputStream = new FileOutputStream(tmp.toFile());
            FileChannel channel = fileOutputStream.getChannel();
            DigestInputStream digestInputStream = new DigestInputStream(in, md)) {

            byte[] buffer = new byte[1024 * 1024];
            int r; 
            while ((r = digestInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, r);
                size += r;
            }
            fileOutputStream.flush();
            channel.force(true);
        } catch (Throwable t) {
            Files.deleteIfExists(tmp);
            throw t;
        }

        // get final locale from hash (content-addressed)
        String sha = toHex(md.digest());
        String shard = sha.substring(0,2) + "/" + sha.substring(2,4);
        Path dir = root.resolve(shard);
        Files.createDirectories(dir);
        Path finalPath = dir.resolve(sha);

        // 3) Atomic finalize (dedupe if already present)
        try {
            Files.move(tmp, finalPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (FileAlreadyExistsException e) {
            Files.deleteIfExists(tmp); 
        }

        String ct = (contentType == null || contentType.isBlank()) ? "application/octet-stream" : contentType;
        return new BlobDTO(sha, size, ct, shard + "/" + sha);
    }

	@Override
    @Transactional
	public Blob upsertBlobRow(BlobDTO response) {
        return blobRepository.findBySha256(response.sha256()).orElseGet(() -> {
            Blob b = new com.personalDrive.model.Blob();
            b.setSha256(response.sha256());
            b.setSize(response.size());
            b.setContentType(response.contentType());
            b.setStoragePath(response.storagePath());

            try {
                return blobRepository.saveAndFlush(b);
            } catch (DataIntegrityViolationException race) {
                return blobRepository.findBySha256(response.sha256()).orElseThrow();
            }
        });
	}

    private static String toHex(byte[] b) {
        var sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
