package com.tiendat.chat_app.controller;

import com.tiendat.chat_app.dto.request.CreateConversationRequest;
import com.tiendat.chat_app.dto.response.ApiResponse;
import com.tiendat.chat_app.dto.response.CreateConversationResponse;
import com.tiendat.chat_app.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping
    public ApiResponse<CreateConversationResponse> createConversation(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateConversationRequest request) {
        String creatorId = jwt.getSubject();
        var data = conversationService.createConversation(creatorId, request);
        return ApiResponse.<CreateConversationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create conversation successfully")
                .data(data)
                .build();
    }
}
