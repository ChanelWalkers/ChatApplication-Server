package com.tiendat.chat_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tiendat.chat_app.common.MessageType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true) // Tạo builder từ object có sẵn
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageResponse {
    private String id; // Message id tu DB
    private String tempId;
    private String conversationId;
    private String conversationAvatar; // Avatar của conversation (cho GROUP)
    private String senderId;
    private String senderName;
    private String content;
    private MessageType messageType;
    private List<MessageMediaResponse> messageMedia;
    private LocalDateTime createdAt;
}
