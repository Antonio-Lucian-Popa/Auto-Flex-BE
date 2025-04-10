package com.asusoftware.AutoFlex.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String userRole;
}
