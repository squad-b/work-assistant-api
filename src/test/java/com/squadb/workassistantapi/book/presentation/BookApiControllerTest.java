package com.squadb.workassistantapi.book.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadb.workassistantapi.book.application.BookService;
import com.squadb.workassistantapi.book.dto.BookRegisterDto;
import com.squadb.workassistantapi.book.infrastructure.BookSearchAgent;
import com.squadb.workassistantapi.member.domain.MemberType;
import com.squadb.workassistantapi.member.dto.LoginMember;
import com.squadb.workassistantapi.rental.domain.NoAuthorizationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookApiController.class)
class BookApiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean BookService bookService;
    @MockBean BookSearchAgent bookSearchAgent;
    private MockHttpSession mockHttpSession;

    @BeforeEach
    public void setup() {
        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("LOGIN_MEMBER", new LoginMember(1L, MemberType.NORMAL));
    }

    @DisplayName("책 등록 파라미터가 유효하지 않으면 INVALID_BODY 란 메시지와 함께 status code 400을 리턴한다.")
    @Test
    public void bookRegisterApiInvalidParameterTest() throws Exception {
        //given
        BookRegisterDto bookRegisterDto = new BookRegisterDto();

        //when
        ResultActions perform = mockMvc.perform(post("/books")
                .session(mockHttpSession)
                .content(objectMapper.writeValueAsString(bookRegisterDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("INVALID_BODY")));
    }

    @DisplayName("책 등록이 정상적으로 완료되면 SUCCESS 란 메시지와 함께 status code 200을 리턴한다")
    @Test
    public void bookRegisterApiSuccessTest() throws Exception {
        //given
        BookRegisterDto bookRegisterDto = new BookRegisterDto();
        bookRegisterDto.setIsbn("1234567890123");
        bookRegisterDto.setTitle("테스트제목");
        bookRegisterDto.setStockQuantity(2);

        final long registerBookId = 1L;
        given(bookService.register(any(BookRegisterDto.class), anyLong())).willReturn(registerBookId);

        //when
        ResultActions perform = mockMvc.perform(post("/books")
                .session(mockHttpSession)
                .content(objectMapper.writeValueAsString(bookRegisterDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SUCCESS")))
                .andExpect(content().string(containsString(String.valueOf(registerBookId))));
    }

    @DisplayName("관리자가 아닌 일반유저가 책 등록을 시도하면 NO_AUTHORIZATION 이란 메시지와 함께 status code 403 을 리턴한다")
    @Test
    public void bookRegisterApiNotAuthorizationTest() throws Exception {
        //given
        BookRegisterDto bookRegisterDto = new BookRegisterDto();
        bookRegisterDto.setIsbn("1234567890123");
        bookRegisterDto.setTitle("테스트제목");
        bookRegisterDto.setStockQuantity(2);

        given(bookService.register(any(BookRegisterDto.class), anyLong())).willThrow(NoAuthorizationException.class);

        //when
        ResultActions perform = mockMvc.perform(post("/books")
                .session(mockHttpSession)
                .content(objectMapper.writeValueAsString(bookRegisterDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("NO_AUTHORIZATION")));
    }
}