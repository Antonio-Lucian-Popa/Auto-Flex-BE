package com.asusoftware.AutoFlex.service;

import com.asusoftware.AutoFlex.model.dto.UserDto;
import com.asusoftware.AutoFlex.model.dto.request.UserRegisterDto;
import com.asusoftware.AutoFlex.model.dto.response.UserResponseDto;

import java.util.UUID;

/**
 * UserService interface for user-related operations.
 */
public interface UserService {
    UserResponseDto getUserById(UUID id);
    UserResponseDto registerUser(UserRegisterDto dto, String keycloakId);
}
