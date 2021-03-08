package com.squadb.workassistantapi.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@RequiredArgsConstructor
@Service
public class BookService {

    // TODO: 인근, 코드 정리하기
    private static final String daumAPIUrl = "https://dapi.kakao.com/v2/search/web";
    private RestTemplate restTemplate;

    @Autowired
    public BookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // TODO: 인근, 코드 정리하기
    public String search(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK 3c01aa602b1860ffec4d5a979a78d78b");

        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8")); headers.setContentType(mediaType);
        headers.setContentType(mediaType);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                daumAPIUrl + "?query=" + query,
                HttpMethod.GET,
                request,
                String.class
        );
        return response.getBody();
    }
}
