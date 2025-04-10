package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.model.User;
import com.asusoftware.AutoFlex.model.UserRole;
import com.asusoftware.AutoFlex.model.dto.request.UserRegisterDto;
import com.asusoftware.AutoFlex.model.dto.response.UserResponseDto;
import com.asusoftware.AutoFlex.repository.UserRepository;
import com.asusoftware.AutoFlex.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow();
        return mapper.map(user, UserResponseDto.class);
    }

    @Override
    public UserResponseDto registerUser(UserRegisterDto dto) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserRole(UserRole.valueOf(dto.getUserRole()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return mapper.map(user, UserResponseDto.class);
    }
}
