package com.tiendat.chat_app.service;

import com.tiendat.chat_app.dto.request.ChatMessageRequest;
import com.tiendat.chat_app.dto.response.ChatMessageResponse;
import com.tiendat.chat_app.dto.response.MessageMediaResponse;
import com.tiendat.chat_app.dto.response.PageResponse;
import com.tiendat.chat_app.entity.ChatMessage;
import com.tiendat.chat_app.entity.Conversation;
import com.tiendat.chat_app.entity.MessageMedia;
import com.tiendat.chat_app.entity.User;
import com.tiendat.chat_app.exception.AppException;
import com.tiendat.chat_app.exception.ErrorCode;
import com.tiendat.chat_app.repository.ChatMessageRepository;
import com.tiendat.chat_app.repository.ConversationRepository;
import com.tiendat.chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(rollbackFor = Exception.class)
    public ChatMessageResponse sendChatMessage(String senderId, ChatMessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Conversation conversation = conversationRepository.findByIdAndMember(request.conversationId(), senderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_CONVERSATION_MEMBER));

        List<MessageMedia> media = request.messageMedia() != null && !request.messageMedia().isEmpty() ?
                request.messageMedia().stream()
                        .map(messageMedia -> MessageMedia.builder()
                                .fileName(messageMedia.fileName())
                                .fileType(messageMedia.fileType())
                                .thumbnailUrl(messageMedia.thumbnailUrl())
                                .build())
                        .toList() : List.of();

        ChatMessage message = ChatMessage.builder()
                .mediaFiles(media)
                .messageType(request.messageType())
                .conversation(conversation)
                .sender(sender)
                .content(request.content())
                .build();

        chatMessageRepository.save(message);

        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageTime(message.getSentAt());
        conversationRepository.save(conversation);

        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .conversationId(message.getConversation().getId())
                .conversationAvatar(message.getConversation().getConversationAvatar())
                .senderName(sender.getUsername())
                .senderId(sender.getId())
                .messageType(message.getMessageType())
                .messageMedia(message.getMediaFiles().stream()
                        .map(messageMedia -> MessageMediaResponse.builder()
                                .fileName(messageMedia.getFileName())
                                .fileType(messageMedia.getFileType())
                                .thumbnailUrl(messageMedia.getThumbnailUrl())
                                .uploadedAt(messageMedia.getUploadedAt())
                                .build())
                        .toList())
                .createdAt(message.getSentAt())
                .build();
    }

    public PageResponse<ChatMessageResponse> getMessages(String conversationId, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String userId = authentication.getName();

        Conversation conversation = conversationRepository.findByIdAndMember(conversationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC,"sentAt"));

        Page<ChatMessage> messagePage = chatMessageRepository.findByConversationId(conversationId,pageable);

        List<ChatMessage> messages = messagePage.getContent();

        List<ChatMessageResponse> responses = messages.stream()
                .map(message -> ChatMessageResponse.builder()
                        .id(message.getId())
                        .content(message.getContent())
                        .conversationId(conversation.getId())
                        .conversationAvatar(conversation.getConversationAvatar())
                        .senderId(userId)
                        .createdAt(message.getSentAt())
                        .senderName(message.getSender().getUsername())
                        .messageMedia(message.getMediaFiles().stream()
                                .map(messageMedia -> MessageMediaResponse.builder()
                                        .fileName(messageMedia.getFileName())
                                        .fileType(messageMedia.getFileType())
                                        .thumbnailUrl(messageMedia.getThumbnailUrl())
                                        .uploadedAt(messageMedia.getUploadedAt())
                                        .build()).toList())
                        .messageType(message.getMessageType())
                        .build())
                .toList();

        return PageResponse.<ChatMessageResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(messagePage.getTotalPages())
                .totalElements(messagePage.getTotalElements())
                .content(responses)
                .build();
    }
}
