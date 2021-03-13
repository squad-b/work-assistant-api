package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookApiController {

    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<String> searchBook(@RequestParam String query) {
        return new ResponseEntity<>(bookService.search(query), HttpStatus.OK);
    }
}
