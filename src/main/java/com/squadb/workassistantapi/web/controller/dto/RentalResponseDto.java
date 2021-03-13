package com.squadb.workassistantapi.web.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RentalResponseDto {
    @Getter private long id;
    @Getter private String result;

    public static RentalResponseDto success(long rentalId) {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.id = rentalId;
        rentalResponseDto.result = "SUCCESS";
        return rentalResponseDto;
    }

    public static RentalResponseDto fail(String message) {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.id = -1;
        rentalResponseDto.result = message;
        return rentalResponseDto;
    }
}
