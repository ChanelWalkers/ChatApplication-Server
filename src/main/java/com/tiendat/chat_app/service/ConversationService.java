package com.tiendat.chat_app.service;

import com.tiendat.chat_app.common.ConversationType;
import com.tiendat.chat_app.dto.request.CreateConversationRequest;
import com.tiendat.chat_app.dto.response.ConversationDetailResponse;
import com.tiendat.chat_app.dto.response.CreateConversationResponse;
import com.tiendat.chat_app.dto.response.PageResponse;
import com.tiendat.chat_app.entity.Conversation;
import com.tiendat.chat_app.entity.User;
import com.tiendat.chat_app.exception.AppException;
import com.tiendat.chat_app.exception.ErrorCode;
import com.tiendat.chat_app.mapper.ConversationMapper;
import com.tiendat.chat_app.repository.ConversationRepository;
import com.tiendat.chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public PageResponse<ConversationDetailResponse> getMyConversation(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Conversation> conversationPage = conversationRepository.findAllByUserId(userId, pageable);

        List<String> conversationIds = conversationPage.getContent().stream()
                .map(Conversation::getId)
                .toList();

        List<Conversation> conversationsWithParticipants = conversationIds.isEmpty() ?
                List.of() : conversationRepository.findByIdInWithParticipants(conversationIds);

        List<ConversationDetailResponse> conversationDetailResponses = conversationsWithParticipants.stream()
                .map(conversation -> ConversationMapper.toConversationDetailResponse(userId, conversation)).toList();

        return PageResponse.<ConversationDetailResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .content(conversationDetailResponses)
                .totalElements(conversationPage.getTotalElements())
                .totalPages(conversationPage.getTotalPages())
                .build();

    }
}
