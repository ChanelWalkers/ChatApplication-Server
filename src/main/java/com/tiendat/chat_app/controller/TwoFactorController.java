package com.tiendat.chat_app.controller;

import com.tiendat.chat_app.configuration.CustomJwtDecoder;
import com.tiendat.chat_app.dto.response.ApiResponse;
import com.tiendat.chat_app.dto.response.LoginResponse;
import com.tiendat.chat_app.dto.response.TwoFaResponse;
import com.tiendat.chat_app.service.TwoFaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/2fa")
@RequiredArgsConstructor
public class TwoFactorController {

    private final CustomJwtDecoder jwtDecoder;
    private final TwoFaService twoFaService;

    @PostMapping("/setup")
    public ApiResponse<TwoFaResponse> setup(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        var data = twoFaService.setup(userId);

        return ApiResponse.<TwoFaResponse>builder()
                .code(CREATED.value())
                .message("2FA has been set up")
                .data(data)
                .build();
    }

    @PostMapping("/enable")
    public ApiResponse enable(@AuthenticationPrincipal Jwt jwt,@RequestParam int otp) {
        String userId = jwt.getSubject();
        var message = twoFaService.enable(userId,otp);

        return ApiResponse.builder()
                .data(null)
                .code(OK.value())
                .message(message)
                .build();
    }

    @PostMapping("/disable")
    public ApiResponse disable(@AuthenticationPrincipal Jwt jwt,@RequestParam int otp) {
        String userId = jwt.getSubject();
        var message = twoFaService.disable(userId,otp);

        return ApiResponse.builder()
                .data(null)
                .code(OK.value())
                .message(message)
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<LoginResponse> verifyOTP(@RequestHeader("2fa-token") String twoFaToken, @RequestParam int otp) {
        Jwt jwt = jwtDecoder.decode(twoFaToken);
        String userId = jwt.getSubject();

        var data = twoFaService.verifyOTP(userId, otp);

        return ApiResponse.<LoginResponse>builder()
                .message("Login successfully")
                .code(OK.value())
                .data(data)
                .build();
    }
}
