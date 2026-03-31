package com.tiendat.chat_app.controller;

import com.tiendat.chat_app.dto.request.LoginRequest;
import com.tiendat.chat_app.dto.request.RefreshTokenRequest;
import com.tiendat.chat_app.dto.response.ApiResponse;
import com.tiendat.chat_app.dto.response.LoginResponse;
import com.tiendat.chat_app.service.AuthenticationService;
import com.tiendat.chat_app.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var data = authenticationService.login(request);
        return ApiResponse.<LoginResponse>builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("Login Successfully")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        var data = authenticationService.refresh(request);
        return ApiResponse.<LoginResponse>builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("Ok")
                .build();
    }
}
