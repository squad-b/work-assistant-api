package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.service.BookService;
import com.squadb.workassistantapi.web.config.auth.LoginMemberId;
import com.squadb.workassistantapi.web.controller.dto.BookRegisterRequestDto;
import com.squadb.workassistantapi.web.controller.dto.BookRegisterResponseDto;
import com.squadb.workassistantapi.web.exception.InvalidRequestBodyException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BookApiController {

    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<String> searchBook(@RequestParam String query) {
        return new ResponseEntity<>(bookService.search(query), HttpStatus.OK);
    }

    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookRegisterResponseDto> registerBook(@RequestBody BookRegisterRequestDto registerRequestDto,
                                                                @LoginMemberId long loginMemberId) {
        long bookId = bookService.register(registerRequestDto.toEntity(), loginMemberId);
        return new ResponseEntity<>(BookRegisterResponseDto.success(bookId), HttpStatus.OK);
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    public ResponseEntity<BookRegisterResponseDto> handlerInvalidRequestBodyException() {
        return new ResponseEntity<>(BookRegisterResponseDto.fail("INVALID_BODY"), HttpStatus.BAD_REQUEST);
    }
}
