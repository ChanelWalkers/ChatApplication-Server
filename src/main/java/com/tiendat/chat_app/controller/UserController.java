package com.tiendat.chat_app.controller;

import com.tiendat.chat_app.dto.request.CreateUserRequest;
import com.tiendat.chat_app.dto.response.ApiResponse;
import com.tiendat.chat_app.dto.response.CreateUserResponse;
import com.tiendat.chat_app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    ApiResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest userRequest) {
        var data = userService.createUser(userRequest);

        return ApiResponse.<CreateUserResponse>builder()
                .data(data)
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .build();
    }

}
