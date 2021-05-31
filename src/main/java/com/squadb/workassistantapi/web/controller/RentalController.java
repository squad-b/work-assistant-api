package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.Rental;
import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.domain.exceptions.OutOfStockException;
import com.squadb.workassistantapi.service.RentalService;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import com.squadb.workassistantapi.web.controller.dto.RentalRequestDto;
import com.squadb.workassistantapi.web.controller.dto.RentalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RentalController {

    public final RentalService rentalService;

    @PostMapping(value = "/rent/books/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentalResponseDto> rentBook(@PathVariable long bookId,
                                                      @CurrentLoginMember LoginMember loginMember,
                                                      @RequestBody(required = false) RentalRequestDto rentalRequestDto) {
        rentalRequestDto = rentalRequestDto == null ? new RentalRequestDto() : rentalRequestDto;
        final long rentalId = rentalService.rentBook(bookId, loginMember.getId(), rentalRequestDto.isLongTerm());
        return ResponseEntity.ok(RentalResponseDto.success(rentalId));
    }

    @GetMapping(value = "/rentals/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RentalResponseDto>> findBookRentals(@PathVariable long bookId) {
        List<Rental> bookRentalList = rentalService.findAllByBook(bookId);
        return ResponseEntity.ok(RentalResponseDto.of(bookRentalList));
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<RentalResponseDto> handleOutOfStock() {
        return ResponseEntity.ok(RentalResponseDto.fail("OUT_OF_STOCK"));
    }

    @ExceptionHandler(NoAuthorizationException.class)
    public ResponseEntity<RentalResponseDto> handleNoAuthorization() {
        return new ResponseEntity<>(RentalResponseDto.fail("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }
}
