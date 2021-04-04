package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.RentalStatus;
import lombok.Getter;

@Getter
public class RentalRequestDto {
    private boolean longTerm;
    private RentalStatus status;
    private int rentCount = 1;
}
