package com.squadb.workassistantapi.member;

import com.squadb.workassistantapi.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("회원 인수 테스트")
public class MemberAcceptanceTest extends AcceptanceTest {

    @Test
    void 회원_등록_테스트() {
        ExtractableResponse<Response> response = 회원_등록_요청("normal@miridih.com", "test1234","유인근", "ADMIN");

        회원_등록됨(response);
    }

    private ExtractableResponse<Response> 회원_등록_요청(String email, String password, String name, String memberType) {
        Map<String, String> body = Map.of(
                "email", email,
                "password", password,
                "name", name,
                "memberType", memberType);

        return RestAssured
                .given()
                    .log().all()
                    .body(body).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/members")
                .then()
                    .log().all().extract();
    }

    private void 회원_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

}
