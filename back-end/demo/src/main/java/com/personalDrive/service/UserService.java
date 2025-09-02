package com.personalDrive.service;

import java.util.Optional;

import com.personalDrive.model.UserDTOs;

public interface UserService {
    boolean emailExists(String email);
    Optional<UserDTOs.UserDTO> getByEmail(String email);
    Optional<UserDTOs.UserEmailDTO> getUserEmailByID(Long id); 
}
