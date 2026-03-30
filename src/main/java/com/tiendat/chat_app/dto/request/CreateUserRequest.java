package com.tiendat.chat_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateUserRequest {
    @Email
    @NotBlank(message = "email is required")
    private String email;

    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password at least 6 characters")
    private String password;
}
