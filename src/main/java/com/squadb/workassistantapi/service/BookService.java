package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@RequiredArgsConstructor
@Service
public class BookService {

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;

    private final BookRepository bookRepository;

    private final MemberService memberService;

    // TODO: 인근, 코드 정리하기
    public String search(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoApiKey);

        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8")); headers.setContentType(mediaType);
        headers.setContentType(mediaType);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoApiUrl + "?query=" + query,
                HttpMethod.GET,
                request,
                String.class
        );
        return response.getBody();
    }

    @Transactional(readOnly = true)
    public Book findById(final long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Book:[%d]", bookId)));
    }

    @Transactional
    public long register(final Book book, final long registrantId) {
        final Member member = memberService.findById(registrantId);
        book.setRegistrant(member);
        final Book saveBook = bookRepository.save(book);
        return saveBook.getId();
    }
}
