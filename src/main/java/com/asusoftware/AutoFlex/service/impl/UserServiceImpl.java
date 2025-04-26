package com.asusoftware.AutoFlex.service.impl;

import com.asusoftware.AutoFlex.config.KeycloakService;
import com.asusoftware.AutoFlex.model.User;
import com.asusoftware.AutoFlex.model.UserRole;
import com.asusoftware.AutoFlex.model.dto.request.LoginDto;
import com.asusoftware.AutoFlex.model.dto.request.UserRegisterDto;
import com.asusoftware.AutoFlex.model.dto.response.UserResponseDto;
import com.asusoftware.AutoFlex.repository.UserRepository;
import com.asusoftware.AutoFlex.service.UserService;
import jakarta.transaction.Transactional;
import org.keycloak.representations.AccessTokenResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final KeycloakService keycloakService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.keycloakService = keycloakService;
    }

    @Override
    public UserResponseDto getUserById(UUID jwtUserId) {
        User user = userRepository.findByKeycloakId(jwtUserId)
                .orElseThrow(() -> new NoSuchElementException("Owner not found"));
        return mapper.map(user, UserResponseDto.class);
    }

    @Override
    @Transactional
    public UserResponseDto register(UserRegisterDto dto) {

        // 3. Creăm user-ul în Keycloak
        String keycloakId = keycloakService.createKeycloakUser(dto);


        // 1. Cream entitatea locală
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(dto.getEmail());
        user.setPassword("protected"); // parola doar în keycloak
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserRole(UserRole.valueOf(dto.getUserRole()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        // 4. Actualizăm entitatea cu ID-ul din Keycloak
        user.setKeycloakId(UUID.fromString(keycloakId));

        // 2. Salvăm local
        userRepository.save(user);

        return mapper.map(user, UserResponseDto.class);
    }

    @Override
    public AccessTokenResponse login(LoginDto loginDto) {
        return keycloakService.loginUser(loginDto);
    }
}
