package com.squadb.workassistantapi.reservation.dto;

import lombok.Builder;

public class PaginationResponseDto {
    private int page;
    private int pageSize;
    private int pageCount;
    private long totalCount;

    @Builder
    public PaginationResponseDto(int page, int pageSize, int pageCount, long totalCount) {
        this.page = page;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.totalCount = totalCount;
    }
}
