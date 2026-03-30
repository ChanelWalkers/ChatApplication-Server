package com.tiendat.chat_app.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private int status;
    private String message;
    private String error;
    private String path;
}
