package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean MemberService memberService;
    MockHttpSession mockSession = new MockHttpSession();

    @DisplayName("로그인 되어있으면 AUTHORIZED")
    @Test
    public void authorizedTest() throws Exception {
        mockSession.setAttribute("MEMBER_ID", 123);

        mockMvc.perform(get("/auth").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(content().string(not("UNAUTHORIZED")))
                .andExpect(content().string(containsString("AUTHORIZED")));
    }

    @DisplayName("로그인 되어있지 않으면 UNAUTHORIZED")
    @Test
    public void notAuthorizedTest() throws Exception {
        mockMvc.perform(get("/auth").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("UNAUTHORIZED")));
    }

}