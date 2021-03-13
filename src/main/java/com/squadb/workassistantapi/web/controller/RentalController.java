package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.exceptions.OutOfStockException;
import com.squadb.workassistantapi.web.config.auth.LoginMemberId;
import com.squadb.workassistantapi.web.controller.dto.RentalRequestDto;
import com.squadb.workassistantapi.service.RentalService;
import com.squadb.workassistantapi.web.controller.dto.RentalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RentalController {

    public final RentalService rentalService;

    @PostMapping(value = "/rent/book/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentalResponseDto> rentBook(@PathVariable long bookId,
                                                      @LoginMemberId long loginMemberId,
                                                      @RequestBody(required = false) RentalRequestDto rentalRequestDto) {
        rentalRequestDto = rentalRequestDto == null ? new RentalRequestDto() : rentalRequestDto;
        final long rentalId = rentalService.rentBook(bookId, loginMemberId, rentalRequestDto.isLongTerm());
        return ResponseEntity.ok(RentalResponseDto.success(rentalId));
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<RentalResponseDto> handleOutOfStock() {
        return ResponseEntity.ok(RentalResponseDto.fail("OUT_OF_STOCK"));
    }
}
