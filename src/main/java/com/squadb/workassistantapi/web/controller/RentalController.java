package com.squadb.workassistantapi.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.squadb.workassistantapi.domain.Rental;
import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.domain.exceptions.OutOfStockException;
import com.squadb.workassistantapi.service.RentalService;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import com.squadb.workassistantapi.web.controller.dto.RentalRequestDto;
import com.squadb.workassistantapi.web.controller.dto.RentalResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RentalController {

    public final RentalService rentalService;

    @PostMapping(value = "/rent/books/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentalResponseDto> rentBook(@PathVariable Long bookId,
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

    @PutMapping(value = "/rentals/{rentalId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentalResponseDto> returnBook(@PathVariable Long rentalId,
                                                        @CurrentLoginMember LoginMember loginMember,
                                                        @RequestBody RentalRequestDto rentalRequestDto) {
        final long returnedRentalId = rentalService.updateRental(rentalId, loginMember.getId(), rentalRequestDto.getStatus());
        return ResponseEntity.ok(RentalResponseDto.success(returnedRentalId));
    }

    @PostMapping(value = "/return/rentals", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RentalResponseDto>> returnBooks(@CurrentLoginMember LoginMember loginMember, @RequestBody RentalRequestDto rentalRequestDto) {
        try {
            final List<Rental> rentalList = rentalService.returnBooks(rentalRequestDto.getRentalIdList(), loginMember);
            return ResponseEntity.ok(RentalResponseDto.of(rentalList));
        } catch (NoAuthorizationException e) {
            return ResponseEntity.ok(List.of(RentalResponseDto.fail("반납 권한이 없습니다.")));
        }
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
