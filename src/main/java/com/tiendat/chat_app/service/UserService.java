package com.tiendat.chat_app.service;

import com.tiendat.chat_app.dto.request.CreateUserRequest;
import com.tiendat.chat_app.dto.response.CreateUserResponse;
import com.tiendat.chat_app.dto.response.PageResponse;
import com.tiendat.chat_app.dto.response.UserDetailResponse;
import com.tiendat.chat_app.entity.Role;
import com.tiendat.chat_app.entity.User;
import com.tiendat.chat_app.exception.AppException;
import com.tiendat.chat_app.exception.ErrorCode;
import com.tiendat.chat_app.repository.RoleRepository;
import com.tiendat.chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tiendat.chat_app.constant.AppConstant.USER_ROLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    // rollback với mọi loại Transaction (checked và cả Unchecked)
    @Transactional(rollbackFor = Exception.class)
    public CreateUserResponse createUser(CreateUserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = User.builder()
                .email(userRequest.getEmail())
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();

        Role role = roleRepository.findByName(USER_ROLE)
                .orElseGet(() -> roleRepository.save(Role.builder()
                                .name(USER_ROLE)
                        .build()));
        user.addRole(role);

        userRepository.save(user);
        return CreateUserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public UserDetailResponse myInfo(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return UserDetailResponse.builder()
                .email(user.getEmail())
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    public PageResponse<UserDetailResponse> searchUsers(String keyword, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String userId = authentication.getName();


        Pageable pageable = PageRequest.of(page - 1, size);

        Page<User> userPage = userRepository.searchUsers(keyword, pageable);

        List<UserDetailResponse> responses = userPage.getContent()
                .stream()
                .filter(u -> !u.getId().equals(userId))
                .map(u -> UserDetailResponse.builder()
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .userId(u.getId())
                        .build())
                .toList();

        return PageResponse.<UserDetailResponse>builder()
                .content(responses)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .pageSize(size)
                .currentPage(page)
                .build();
    }
}
