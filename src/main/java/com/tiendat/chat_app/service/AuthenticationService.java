package com.tiendat.chat_app.service;

import com.tiendat.chat_app.common.TokenType;
import com.tiendat.chat_app.configuration.CustomJwtDecoder;
import com.tiendat.chat_app.dto.request.LoginRequest;
import com.tiendat.chat_app.dto.request.RefreshTokenRequest;
import com.tiendat.chat_app.dto.response.LoginResponse;
import com.tiendat.chat_app.entity.User;
import com.tiendat.chat_app.exception.AppException;
import com.tiendat.chat_app.exception.ErrorCode;
import com.tiendat.chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomJwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Set<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(user.getId(), authorities);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if(!isValidRefreshToken(refreshToken, TokenType.REFRESH_TOKEN)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        Jwt jwt = jwtDecoder.decode(refreshToken);

        String userId = jwt.getSubject();

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Set<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(user.getId(), authorities);
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


    private boolean isValidRefreshToken(String token, TokenType tokenType) {
        Jwt jwt = jwtDecoder.decode(token);
        String tokenTypeFromClaims = jwt.getClaim("token_type");
        return tokenTypeFromClaims.equals(tokenType.toString());
    }
}
