package com.squadb.workassistantapi.web.agent;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@RequiredArgsConstructor
@Component
public class BookSearchAgent {

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;

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

}
