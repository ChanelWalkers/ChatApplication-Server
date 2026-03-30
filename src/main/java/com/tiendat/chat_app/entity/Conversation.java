package com.tiendat.chat_app.entity;

import com.tiendat.chat_app.common.ConversationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(name = "conversation_avatar")
    private String conversationAvatar;

    private ConversationType conversationType;

    @OneToMany(mappedBy = "conversation",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ConversationParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    @Column(name = "participant_hash", unique = true)
    private String participantHash;

    private LocalDateTime createdAt;

    private LocalDateTime lastMessageTime;

    @Column(name = "last_message_id")
    private String lastMessageId;

    @Column(name = "last_message_content")
    private String lastMessageContent;
}
