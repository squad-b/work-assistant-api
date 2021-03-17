package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.service.BookService;
import com.squadb.workassistantapi.web.agent.BookSearchAgent;
import com.squadb.workassistantapi.web.agent.dto.BookSearchRequestDto;
import com.squadb.workassistantapi.web.agent.dto.BookSearchResponseDto;
import com.squadb.workassistantapi.web.config.auth.LoginMemberId;
import com.squadb.workassistantapi.web.controller.dto.BookRegisterRequestDto;
import com.squadb.workassistantapi.web.controller.dto.BookRegisterResponseDto;
import com.squadb.workassistantapi.web.exception.InvalidRequestBodyException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
public class BookApiController {

    private final BookService bookService;
    private final BookSearchAgent bookSearchAgent;

    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookSearchResponseDto> searchBook(BookSearchRequestDto request) {
        ResponseEntity<BookSearchResponseDto> search = bookSearchAgent.search(request);
        return ResponseEntity.ok(Objects.requireNonNull(search.getBody()));
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

    @ExceptionHandler(NoAuthorizationException.class)
    public ResponseEntity<BookRegisterResponseDto> handleNoAuthorizationException() {
        return new ResponseEntity<>(BookRegisterResponseDto.fail("NO_AUTHORIZATION"), HttpStatus.FORBIDDEN);
    }
}
