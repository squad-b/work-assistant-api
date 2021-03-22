package com.squadb.workassistantapi.web.controller.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.*;
import com.squadb.workassistantapi.domain.Rental;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RentalResponseDto {
    private long id;
    private String result;
    private String borrowerName;
    private Boolean isLongTerm;
    private String startDate;
    private String endDate;

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

    private static RentalResponseDto of(Rental bookRental) {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.id = bookRental.getId();
        rentalResponseDto.borrowerName = bookRental.getMemberName();
        rentalResponseDto.isLongTerm = bookRental.isLongTerm();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        rentalResponseDto.startDate = bookRental.getStartDate().format(formatter);
        rentalResponseDto.endDate = bookRental.getEndDate() == null ? "" : bookRental.getEndDate().format(formatter);
        return rentalResponseDto;
    }

    public static List<RentalResponseDto> of(List<Rental> bookRentalList) {
        return bookRentalList.stream().map(bookRental -> RentalResponseDto.of(bookRental)).collect(Collectors.toList());
    }
}
