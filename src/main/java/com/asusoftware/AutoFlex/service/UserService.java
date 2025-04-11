package com.asusoftware.AutoFlex.service;

import com.asusoftware.AutoFlex.model.dto.UserDto;
import com.asusoftware.AutoFlex.model.dto.request.LoginDto;
import com.asusoftware.AutoFlex.model.dto.request.UserRegisterDto;
import com.asusoftware.AutoFlex.model.dto.response.UserResponseDto;
import org.keycloak.representations.AccessTokenResponse;

import java.util.UUID;

/**
 * UserService interface for user-related operations.
 */
public interface UserService {
    UserResponseDto getUserById(UUID id);
    UserResponseDto register(UserRegisterDto dto);
    AccessTokenResponse login(LoginDto loginDto);
}
