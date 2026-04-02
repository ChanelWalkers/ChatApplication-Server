package com.tiendat.chat_app.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
@Setter
public class PageResponse<T> implements Serializable {
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private int pageSize;

    @Builder.Default
    private List<T> content = Collections.emptyList();
}
