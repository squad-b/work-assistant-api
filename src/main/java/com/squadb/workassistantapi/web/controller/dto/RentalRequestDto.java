package com.squadb.workassistantapi.web.controller.dto;

import lombok.Getter;

@Getter
public class RentalRequestDto {
    private boolean longTerm;

    public static RentalRequestDto of(boolean longTerm) {
        RentalRequestDto rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.longTerm = longTerm;
        return rentalRequestDto;
    }
}
