package com.tiendat.chat_app.mapper;

import com.tiendat.chat_app.common.ConversationType;
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

        if(conversationType == ConversationType.GROUP) {
            response.setConversationAvatar(conversation.getConversationAvatar());
            response.setName(conversation.getName());
        }else {
            conversation.getParticipants()
                    .stream()
                    .filter(participant -> participant.getUser().getId().equals(creatorId))
                    .findFirst()
                    .ifPresent(participantInfo -> response.setName(participantInfo.getUser().getUsername()));
        }
        return response;
    }
}
