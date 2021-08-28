package com.squadb.workassistantapi.reservation.dto;

import com.squadb.workassistantapi.reservation.domain.Reservation;
import lombok.Builder;
import org.springframework.data.domain.Page;

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

    public static PaginationResponseDto from(Page<Reservation> pageResult) {
        new PaginationResponseDto(pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalPages(), pageResult.getTotalElements());

        return PaginationResponseDto.builder()
                .page(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .pageCount(pageResult.getTotalPages())
                .totalCount(pageResult.getTotalElements())
                .build();
    }

}
