package com.personalDrive.model;

public class UserDTOs {
    public record UserDTO(Long id, String email) {
    }

    public record UserEmailDTO(String email) {
    }
}
