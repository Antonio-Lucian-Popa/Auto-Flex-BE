package com.asusoftware.AutoFlex.controller;

import com.asusoftware.AutoFlex.config.KeycloakService;
import com.asusoftware.AutoFlex.model.dto.request.LoginDto;
import com.asusoftware.AutoFlex.model.dto.request.UserRegisterDto;
import com.asusoftware.AutoFlex.model.dto.response.UserResponseDto;
import com.asusoftware.AutoFlex.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            var tokenResponse = userService.login(loginDto);
            return ResponseEntity.ok(tokenResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal Jwt jwt) {
        UUID id = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(userService.getUserById(id));
    }
}