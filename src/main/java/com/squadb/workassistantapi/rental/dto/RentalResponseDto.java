package com.squadb.workassistantapi.rental.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.squadb.workassistantapi.rental.domain.Rental;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class RentalResponseDto {
    private Long id;
    private String result;
    private String borrowerName;
    private String bookTitle;
    private Boolean isLongTerm;
    private String startDate;
    private String endDate;

    public static RentalResponseDto success() {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.result = "SUCCESS";
        return rentalResponseDto;
    }

    public static RentalResponseDto success(Long rentalId) {
        RentalResponseDto rentalResponseDto = success();
        rentalResponseDto.id = rentalId;
        return rentalResponseDto;
    }

    public static RentalResponseDto fail(String message) {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.id = -1L;
        rentalResponseDto.result = message;
        return rentalResponseDto;
    }

    private static RentalResponseDto of(Rental bookRental) {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.id = bookRental.getId();
        rentalResponseDto.borrowerName = bookRental.getMemberName();
        rentalResponseDto.bookTitle = bookRental.getBookTitle();
        rentalResponseDto.isLongTerm = bookRental.isLongTerm();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        rentalResponseDto.startDate = bookRental.getStartDate().format(formatter);
        rentalResponseDto.endDate = bookRental.getEndDate() == null ? "" : bookRental.getEndDate().format(formatter);
        return rentalResponseDto;
    }

    public static List<RentalResponseDto> of(List<Rental> bookRentalList) {
        return bookRentalList.stream().map(RentalResponseDto::of).collect(Collectors.toList());
    }
}
