package com.tiendat.chat_app.controller;

import com.tiendat.chat_app.configuration.CustomJwtDecoder;
import com.tiendat.chat_app.dto.request.CreateUserRequest;
import com.tiendat.chat_app.dto.response.ApiResponse;
import com.tiendat.chat_app.dto.response.CreateUserResponse;
import com.tiendat.chat_app.dto.response.PageResponse;
import com.tiendat.chat_app.dto.response.UserDetailResponse;
import com.tiendat.chat_app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    ApiResponse<UserDetailResponse> myInfo(@AuthenticationPrincipal Jwt jwt) {
        var userId = jwt.getSubject();

        var data = userService.myInfo(userId);

        return ApiResponse.<UserDetailResponse>builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("User info retrieved successfully")
                .build();
    }

    @GetMapping("/search")
    ApiResponse<PageResponse<UserDetailResponse>> searchUsers(@RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "5") int size,
                                          @RequestParam(required = false) String keyword){
        var data = userService.searchUsers(keyword,page, size);

        return ApiResponse.<PageResponse<UserDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(data)
                .build();
    }

}
