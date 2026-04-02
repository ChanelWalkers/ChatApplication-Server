package com.tiendat.chat_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tiendat.chat_app.common.ConversationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationDetailResponse {
    private String id;
    private String name;
    private ConversationType conversationType;
    private String conversationAvatar;
    private List<ParticipantResponse> participantInfos;



    private String lastMessageId;
    private String lastMessageContent;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageTime;
}
