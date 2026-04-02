package com.tiendat.chat_app.controller;

import com.tiendat.chat_app.dto.request.ChatMessageRequest;
import com.tiendat.chat_app.dto.response.ApiResponse;
import com.tiendat.chat_app.dto.response.ChatMessageResponse;
import com.tiendat.chat_app.dto.response.PageResponse;
import com.tiendat.chat_app.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat-messages")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping
    ApiResponse<ChatMessageResponse> chatMessage(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ChatMessageRequest request){
        var userId = jwt.getSubject();

        var data = chatMessageService.sendChatMessage(userId,request);

        return ApiResponse.<ChatMessageResponse>builder()
                .data(data)
                .code(HttpStatus.CREATED.value())
                .message("Chat message sent successfully")
                .build();
    }

    @GetMapping("/conversations/{conversationId}/messages")
    ApiResponse<PageResponse<ChatMessageResponse>> getMessages(@PathVariable(name = "conversationId") String conversationId,
                                                               @RequestParam(required = false, defaultValue = "1") int page,
                                                               @RequestParam(required = false, defaultValue = "10") int size) {
        var data = chatMessageService.getMessages(conversationId,page, size);

        return ApiResponse.<PageResponse<ChatMessageResponse>>builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("Messages retrieved successfully!")
                .build();
    }
}
