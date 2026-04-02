package com.tiendat.chat_app.dto.request;

public record MessageMediaRequest(
        String fileName,
        String fileType,
        String thumbnailUrl
) {
}
