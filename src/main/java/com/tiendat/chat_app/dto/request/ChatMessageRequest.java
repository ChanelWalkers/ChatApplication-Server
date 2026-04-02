package com.tiendat.chat_app.dto.request;

import com.tiendat.chat_app.common.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatMessageRequest(
        String tempId,

        @NotBlank(message = "Conversation id is required")
        String conversationId,

        String content,

        @NotNull(message = "Message Type is required")
        MessageType messageType,

        List<MessageMediaRequest> messageMedia
) {
}
