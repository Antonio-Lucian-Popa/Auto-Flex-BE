package com.asusoftware.AutoFlex.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BasicOwnerDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Instant createdAt;
}
