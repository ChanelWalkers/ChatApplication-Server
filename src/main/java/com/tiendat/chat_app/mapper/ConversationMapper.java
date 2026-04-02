package com.tiendat.chat_app.mapper;

import com.tiendat.chat_app.common.ConversationType;
import com.tiendat.chat_app.dto.response.ConversationDetailResponse;
import com.tiendat.chat_app.dto.response.CreateConversationResponse;
import com.tiendat.chat_app.dto.response.ParticipantResponse;
import com.tiendat.chat_app.entity.Conversation;

public final class ConversationMapper {
    private ConversationMapper() {

    }

    public static CreateConversationResponse toConversationResponse(String creatorId, Conversation conversation) {
        ConversationType conversationType = conversation.getConversationType();

        CreateConversationResponse response = CreateConversationResponse.builder()
                .id(conversation.getId())
                .conversationType(conversationType.toString())
                .createdAt(conversation.getCreatedAt())
                .participantInfo(conversation.getParticipants().stream().map(conversationParticipant -> ParticipantResponse.builder()
                                .userId(conversationParticipant.getUser().getId())
                                .userName(conversationParticipant.getUser().getUsername())
                                .build()).toList())
                .build();

        String name = resolveConversationName(creatorId, conversation);
        response.setName(name);

        if(conversationType == ConversationType.GROUP) {
            response.setConversationAvatar(conversation.getConversationAvatar());
            response.setName(conversation.getName());
        }
        return response;
    }

    public static ConversationDetailResponse toConversationDetailResponse(String creatorId, Conversation conversation) {
        ConversationType conversationType = conversation.getConversationType();

        ConversationDetailResponse response = ConversationDetailResponse.builder()
                .id(conversation.getId())
                .lastMessageTime(conversation.getLastMessageTime())
                .lastMessageContent(conversation.getLastMessageContent())
                .conversationType(conversationType)
                .lastMessageId(conversation.getLastMessageId())
                .createdAt(conversation.getCreatedAt())
                .participantInfos(conversation.getParticipants().stream().map(participant -> ParticipantResponse.builder()
                        .userId(participant.getUser().getId())
                        .userName(participant.getUser().getUsername())
                        .build()).toList())
                .build();

        String name = resolveConversationName(creatorId, conversation);
        response.setName(name);

        if(conversationType == ConversationType.GROUP) {
            response.setConversationAvatar(conversation.getConversationAvatar());
        }

        return response;
    }

    private static String resolveConversationName(String creatorId, Conversation conversation) {
        if(conversation.getConversationType() == ConversationType.PRIVATE) {
            return conversation.getParticipants().stream()
                    .filter(user -> !user.getUser().getId().equals(creatorId))
                    .findFirst()
                    .map(p -> p.getUser().getUsername())
                    .orElse(null);
        }
        return conversation.getName();
    }
}
