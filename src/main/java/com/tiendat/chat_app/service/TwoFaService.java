package com.tiendat.chat_app.service;

import com.tiendat.chat_app.configuration.CustomJwtDecoder;
import com.tiendat.chat_app.dto.response.ApiResponse;
import com.tiendat.chat_app.dto.response.LoginResponse;
import com.tiendat.chat_app.dto.response.TwoFaResponse;
import com.tiendat.chat_app.entity.User;
import com.tiendat.chat_app.exception.AppException;
import com.tiendat.chat_app.exception.ErrorCode;
import com.tiendat.chat_app.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TWO FA SERVICE")
public class TwoFaService {

    private final GoogleAuthenticatorService gAuthService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public TwoFaResponse setup(String userId) {
        User user = findUserById(userId);
        String qrUrl = null;
        if (Boolean.FALSE.equals(user.getTwoFaEnabled())) {
            if (!StringUtils.hasLength(user.getTwoFaSecret())) {
                GoogleAuthenticatorKey gAuthKey = gAuthService.createCredentials();
                String secretKey = gAuthService.getKey(gAuthKey);
                user.setTwoFaSecret(secretKey);
                userRepository.save(user);

                qrUrl = gAuthService.getQRBarUrl(user.getEmail(), gAuthKey);
            }
        } else {
            qrUrl = gAuthService.getQRBarUrl(user.getEmail(), new GoogleAuthenticatorKey.Builder(user.getTwoFaSecret()).build());
        }

        return TwoFaResponse.builder()
                .QRUrl(qrUrl)
                .build();
    }

    public String enable(String userId,int otp) {
        User user = findUserById(userId);

        if (!StringUtils.hasLength(user.getTwoFaSecret())) {
            throw new AppException(ErrorCode.TWO_FA_NOT_SET_UP);
        }

        if (gAuthService.verifyCode(user.getTwoFaSecret(), otp)) {
            user.setTwoFaEnabled(true);
            userRepository.save(user);

            return "Two Factor is enabled";
        } else {
            throw new AppException(ErrorCode.INVALID_OTP_2FA);
        }
    }

    public String disable( String userId,int otp) {
        User user = findUserById(userId);

        if (!Boolean.TRUE.equals(user.getTwoFaEnabled())) {
            throw new AppException(ErrorCode.TWO_FA_NOT_ENABLED);
        }

        if (gAuthService.verifyCode(user.getTwoFaSecret(), otp)) {
            user.setTwoFaEnabled(false);
            user.setTwoFaSecret(null);
            userRepository.save(user);

            return "Two Factor is disabled";
        } else {
            throw new AppException(ErrorCode.INVALID_OTP_2FA);
        }
    }

    public LoginResponse verifyOTP(String userId, int otp) {
        User user = findUserById(userId);

        if (Boolean.FALSE.equals(user.getTwoFaEnabled())) {
           throw new AppException(ErrorCode.TWO_FA_INACTIVE);
        }

        if(!gAuthService.verifyCode(user.getTwoFaSecret(),otp)) {
            throw new AppException(ErrorCode.INVALID_OTP_2FA);
        }

        Set<String> authorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(userId, authorities))
                .refreshToken(jwtService.generateRefreshToken(userId))
                .build();
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }
}
