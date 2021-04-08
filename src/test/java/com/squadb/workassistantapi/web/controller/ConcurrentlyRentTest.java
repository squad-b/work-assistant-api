package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.web.controller.dto.LoginRequestDto;
import com.squadb.workassistantapi.web.controller.dto.LoginResponseDto;
import com.squadb.workassistantapi.web.controller.dto.RentalRequestDto;
import com.squadb.workassistantapi.web.controller.dto.RentalResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT // 실제 서블릿 환경을 구성하기 위해 random port environment
)
@Sql("classpath:test_data.sql") // 테스트를 위한 데이터를 집어 넣는다.
public class ConcurrentlyRentTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("동시에 같은 책 대여 요청이 들어와도 하나의 Rental 만 생성이 되어야 한다.")
    public void rentConcurrentlyTest() throws InterruptedException {
        final List<String> sessionCookie = login();
        final int requestCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        List<ResponseEntity<RentalResponseDto>> responseDtoList = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < requestCount; i++) {
            executorService.execute(() -> responseDtoList.add(rentBook(sessionCookie)));
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(responseDtoList.stream().filter(res -> res.getStatusCode().is2xxSuccessful()).count()).isEqualTo(1);
    }


    private List<String> login() {
        LoginRequestDto loginRequestDto = LoginRequestDto.of("admin", "admin");
        HttpEntity<LoginRequestDto> requestEntity = new HttpEntity<>(loginRequestDto);
        ResponseEntity<LoginResponseDto> responseEntity = restTemplate.exchange("/login", HttpMethod.POST, requestEntity, LoginResponseDto.class);
        HttpHeaders headers = responseEntity.getHeaders();
        return headers.get(HttpHeaders.SET_COOKIE);
    }

    private ResponseEntity<RentalResponseDto> rentBook(List<String> sessionCookie) {
        RentalRequestDto rentalRequestDto = RentalRequestDto.of(false);
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, sessionCookie);
        HttpEntity<RentalRequestDto> requestEntity = new HttpEntity<>(rentalRequestDto, headers);
        return restTemplate.exchange("/rent/books/1", HttpMethod.POST, requestEntity, RentalResponseDto.class);
    }
}
