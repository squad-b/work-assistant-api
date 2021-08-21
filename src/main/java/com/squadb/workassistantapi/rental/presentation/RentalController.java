package com.squadb.workassistantapi.rental.presentation;

import com.squadb.workassistantapi.book.domain.OutOfStockException;
import com.squadb.workassistantapi.member.dto.LoginMember;
import com.squadb.workassistantapi.member.infrastructure.config.CurrentLoginMember;
import com.squadb.workassistantapi.rental.application.RentalService;
import com.squadb.workassistantapi.rental.domain.NoAuthorizationException;
import com.squadb.workassistantapi.rental.domain.Rental;
import com.squadb.workassistantapi.rental.dto.RentalRequestDto;
import com.squadb.workassistantapi.rental.dto.RentalResponseDto;
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
    public ResponseEntity<RentalResponseDto> rentBook(@PathVariable Long bookId,
                                                      @CurrentLoginMember LoginMember loginMember,
                                                      @RequestBody(required = false) RentalRequestDto rentalRequestDto) {
        rentalRequestDto = rentalRequestDto == null ? new RentalRequestDto() : rentalRequestDto;
        final Long rentalId = rentalService.rentBook(bookId, loginMember.getId(), rentalRequestDto.isLongTerm());
        return ResponseEntity.ok(RentalResponseDto.success(rentalId));
    }

    @GetMapping(value = "/rentals/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RentalResponseDto>> findBookRentals(@PathVariable long bookId) {
        List<Rental> bookRentalList = rentalService.findAllByBook(bookId);
        return ResponseEntity.ok(RentalResponseDto.of(bookRentalList));
    }

    @PostMapping(value = "/return/rentals", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentalResponseDto> returnBooks(@CurrentLoginMember LoginMember loginMember, @RequestBody RentalRequestDto rentalRequestDto) {
        try {
            rentalService.returnBooks(rentalRequestDto.getRentalIdList(), loginMember);
            return ResponseEntity.ok(RentalResponseDto.success());
        } catch (NoAuthorizationException e) {
            return ResponseEntity.ok(RentalResponseDto.fail("반납 권한이 없습니다."));
        }
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<RentalResponseDto> handleOutOfStock() {
        return new ResponseEntity<>(RentalResponseDto.fail("OUT_OF_STOCK"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAuthorizationException.class)
    public ResponseEntity<RentalResponseDto> handleNoAuthorization() {
        return new ResponseEntity<>(RentalResponseDto.fail("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }
}
