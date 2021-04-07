package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.RentalStatus;
import lombok.Getter;

@Getter
public class RentalRequestDto {
    private boolean longTerm;
    private RentalStatus status;

    public static RentalRequestDto of (boolean longTerm) {
        RentalRequestDto rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.longTerm = longTerm;
        return rentalRequestDto;
    }
}
