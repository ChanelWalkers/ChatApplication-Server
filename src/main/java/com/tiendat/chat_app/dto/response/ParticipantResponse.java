package com.tiendat.chat_app.dto.response;

import lombok.Builder;

@Builder
public record ParticipantResponse(
        String userId,
        String userName
) {
}
