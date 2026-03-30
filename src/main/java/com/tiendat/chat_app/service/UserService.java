package com.tiendat.chat_app.service;

import com.tiendat.chat_app.dto.request.CreateUserRequest;
import com.tiendat.chat_app.dto.response.CreateUserResponse;
import com.tiendat.chat_app.entity.Role;
import com.tiendat.chat_app.entity.User;
import com.tiendat.chat_app.exception.AppException;
import com.tiendat.chat_app.exception.ErrorCode;
import com.tiendat.chat_app.repository.RoleRepository;
import com.tiendat.chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
