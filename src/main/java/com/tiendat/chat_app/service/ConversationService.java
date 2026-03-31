package com.tiendat.chat_app.service;

import com.tiendat.chat_app.common.ConversationType;
import com.tiendat.chat_app.dto.request.CreateConversationRequest;
import com.tiendat.chat_app.dto.response.CreateConversationResponse;
import com.tiendat.chat_app.entity.Conversation;
import com.tiendat.chat_app.entity.User;
import com.tiendat.chat_app.exception.AppException;
import com.tiendat.chat_app.exception.ErrorCode;
import com.tiendat.chat_app.mapper.ConversationMapper;
import com.tiendat.chat_app.repository.ConversationRepository;
import com.tiendat.chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public CreateConversationResponse createConversation(String creatorId, CreateConversationRequest request) {
        List<String> participantIds = request.participantIds();

        if (!participantIds.contains(creatorId)) {
            participantIds.add(creatorId);
        }

        List<User> participantInfos = userRepository.findAllById(participantIds);

        if(participantInfos.size() != participantIds.size()) {
            throw new AppException(ErrorCode.PARTICIPANT_NOT_FOUND);
        }

        ConversationType conversationType = request.conversationType();
        String participantHash  = null;

        if(conversationType == ConversationType.PRIVATE) {
            if(participantInfos.size() != 2) {
                throw new AppException(ErrorCode.INVALID_PARTICIPANT_COUNT);
            }

            participantHash = participantInfos.stream()
                    .map(User::getId)
                    .sorted()
                    .collect(Collectors.joining("_"));

            Optional<Conversation> existConversation = conversationRepository.findByParticipantHash(participantHash);

            if(existConversation.isPresent()) {
                return ConversationMapper.toConversationResponse(creatorId, existConversation.get());
            }
        }

        if(conversationType == ConversationType.GROUP) {
            if(request.name().trim().isEmpty()) {
                throw new AppException(ErrorCode.CONVERSATION_NAME_REQUIRED);
            }

            if(participantIds.size() < 3) {
                throw new AppException(ErrorCode.GROUP_CONVERSATION_MINIMUM_THREE_PARTICIPANTS);
            }
        }

        Conversation conversation = Conversation.builder()
                .conversationType(conversationType)
                .createdAt(LocalDateTime.now())
                .name(request.name())
                .conversationAvatar(request.conversationAvatar())
                .participantHash(participantHash)
                .build();

        participantInfos.forEach(conversation::addParticipants);

        conversationRepository.save(conversation);

        return ConversationMapper.toConversationResponse(creatorId, conversation);
    }
}
