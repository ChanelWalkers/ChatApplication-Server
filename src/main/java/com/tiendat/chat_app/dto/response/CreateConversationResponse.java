package com.tiendat.chat_app.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class CreateConversationResponse {
    private String id;
    private String name;
    private String conversationAvatar;
    private String conversationType;
    private List<ParticipantResponse> participantInfo;
    private LocalDateTime createdAt;
}
