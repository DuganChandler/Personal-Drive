package com.personalDrive.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.personalDrive.model.UserDTOs;
import com.personalDrive.model.UserDTOs.UserEmailDTO;
import com.personalDrive.repository.UserRepository;

@Service
public class UserServiceFunc implements UserService{
    private final UserRepository userRepository;

    public UserServiceFunc(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<UserDTOs.UserDTO> getByEmail(String email) {
        return userRepository.findByEmail(email)
               .map(u -> new UserDTOs.UserDTO(u.getId(), u.getEmail()));
    }

    @Override
    public Optional<UserEmailDTO> getUserEmailByID(Long id) {
        return userRepository.findById(id)
                .map(u -> new UserDTOs.UserEmailDTO(u.getEmail()));
    } 
}
