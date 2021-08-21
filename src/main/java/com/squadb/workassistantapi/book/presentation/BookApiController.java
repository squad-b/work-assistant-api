package com.squadb.workassistantapi.book.presentation;

import com.squadb.workassistantapi.book.application.BookService;
import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.book.infrastructure.BookSearchAgent;
import com.squadb.workassistantapi.book.infrastructure.dto.BookSearchRequestDto;
import com.squadb.workassistantapi.book.infrastructure.dto.BookSearchResponseDto;
import com.squadb.workassistantapi.book.presentation.dto.BookDetailResponseDto;
import com.squadb.workassistantapi.book.presentation.dto.BookListResponseDto;
import com.squadb.workassistantapi.book.presentation.dto.BookRegisterDto;
import com.squadb.workassistantapi.book.presentation.dto.BookRegisterResponseDto;
import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.service.exception.KeyDuplicationException;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BookRegisterResponseDto> handlerInvalidRequestBodyException(IllegalArgumentException e) {
        log.error("잘못된 파라미터 입니다.", e);
        return new ResponseEntity<>(BookRegisterResponseDto.fail("INVALID_BODY"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(KeyDuplicationException.class)
    public ResponseEntity<BookRegisterResponseDto> handleKeyDuplicationException(KeyDuplicationException e) {
        log.error("식별자가 중복됐습니다.", e);
        return new ResponseEntity<>(BookRegisterResponseDto.fail("KEY_DUPLICATION"), HttpStatus.OK);
    }

    @ExceptionHandler(NoAuthorizationException.class)
    public ResponseEntity<BookRegisterResponseDto> handleNoAuthorizationException(NoAuthorizationException e) {
        log.error("권한이 없습니다.", e);
        return new ResponseEntity<>(BookRegisterResponseDto.fail("NO_AUTHORIZATION"), HttpStatus.FORBIDDEN);
    }
}
