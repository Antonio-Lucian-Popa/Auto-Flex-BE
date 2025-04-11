package com.asusoftware.AutoFlex.model.dto;

import com.asusoftware.AutoFlex.model.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole userType;
}
