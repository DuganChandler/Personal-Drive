package com.personalDrive.model;

public class FileDTOs {
    public record  FileDTO(
        Long id, 
        Folder folder, 
        User owner, 
        String name 
    ) {}
}
