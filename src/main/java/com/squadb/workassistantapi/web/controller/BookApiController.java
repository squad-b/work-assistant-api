package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.service.BookService;
import com.squadb.workassistantapi.service.exception.KeyDuplicationException;
import com.squadb.workassistantapi.web.agent.BookSearchAgent;
import com.squadb.workassistantapi.web.agent.dto.BookSearchRequestDto;
import com.squadb.workassistantapi.web.agent.dto.BookSearchResponseDto;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.*;
import com.squadb.workassistantapi.web.exception.InvalidRequestBodyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
public class BookApiController {

    private final BookService bookService;
    private final BookSearchAgent bookSearchAgent;

    @GetMapping(value = "/search/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookSearchResponseDto> searchBook(BookSearchRequestDto request) {
        ResponseEntity<BookSearchResponseDto> search = bookSearchAgent.search(request);
        return ResponseEntity.ok(Objects.requireNonNull(search.getBody()));
    }

    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookRegisterResponseDto> registerBook(@RequestBody BookRegisterDto registerRequestDto,
                                                                @CurrentLoginMember LoginMember loginMember) {
        registerRequestDto.checkValidation();
        Long bookId = bookService.register(registerRequestDto, loginMember.getId());
        return new ResponseEntity<>(BookRegisterResponseDto.success(bookId), HttpStatus.OK);
    }

    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookListResponseDto>> findAll() {
        List<Book> bookList = bookService.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return new ResponseEntity<>(BookListResponseDto.of(bookList), HttpStatus.OK);
    }

    @GetMapping(value = "/books/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookDetailResponseDto> findOne(@PathVariable long id) {
        Book book = bookService.findById(id);
        return new ResponseEntity<>(BookDetailResponseDto.of(book), HttpStatus.OK);
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    public ResponseEntity<BookRegisterResponseDto> handlerInvalidRequestBodyException() {
        return new ResponseEntity<>(BookRegisterResponseDto.fail("INVALID_BODY"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(KeyDuplicationException.class)
    public ResponseEntity<BookRegisterResponseDto> handleKeyDuplicationException() {
        return new ResponseEntity<>(BookRegisterResponseDto.fail("KEY_DUPLICATION"), HttpStatus.OK);
    }

    @ExceptionHandler(NoAuthorizationException.class)
    public ResponseEntity<BookRegisterResponseDto> handleNoAuthorizationException() {
        return new ResponseEntity<>(BookRegisterResponseDto.fail("NO_AUTHORIZATION"), HttpStatus.FORBIDDEN);
    }
}
