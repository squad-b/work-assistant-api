package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.RentalStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class RentalRequestDto {
    private boolean longTerm;
    @Setter
    private RentalStatus status;
    private List<Long> rentalIdList;

    public static RentalRequestDto of(boolean longTerm) {
        RentalRequestDto rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.longTerm = longTerm;
        return rentalRequestDto;
    }
}
