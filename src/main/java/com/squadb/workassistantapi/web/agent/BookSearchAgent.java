package com.squadb.workassistantapi.web.agent;

import com.squadb.workassistantapi.web.agent.dto.BookSearchRequestDto;
import com.squadb.workassistantapi.web.agent.dto.BookSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class BookSearchAgent {

    @Value("${book.api.url}")
    private String bookApiUrl;

    @Value("${book.api.auth-type}")
    private String bookApiAuthType;

    @Value("${book.api.key}")
    private String bookApiKey;

    private final RestTemplate restTemplate;

    public ResponseEntity<BookSearchResponseDto> search(BookSearchRequestDto bookSearchRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bookApiAuthType + ' ' + bookApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookSearchRequestDto> bookSearchParamsHttpEntity = new HttpEntity<>(bookSearchRequest, headers);

        return restTemplate.exchange(
                bookSearchRequest.makeUrl(bookApiUrl),
                HttpMethod.GET,
                bookSearchParamsHttpEntity,
                BookSearchResponseDto.class);
    }

}
