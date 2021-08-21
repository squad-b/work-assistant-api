package com.squadb.workassistantapi.book.presentation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookRegisterResponseDto {

    private static final long FAIL_BOOK_ID = -1;

    @Getter private Long id;
    @Getter private String result;

    public static BookRegisterResponseDto success(long bookId) {
        BookRegisterResponseDto bookRegisterResponseDto = new BookRegisterResponseDto();
        bookRegisterResponseDto.id = bookId;
        bookRegisterResponseDto.result = "SUCCESS";
        return bookRegisterResponseDto;
    }

    public static BookRegisterResponseDto fail(String result) {
        BookRegisterResponseDto bookRegisterResponseDto = new BookRegisterResponseDto();
        bookRegisterResponseDto.id = FAIL_BOOK_ID;
        bookRegisterResponseDto.result = result;
        return bookRegisterResponseDto;
    }
}
