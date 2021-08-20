package com.squadb.workassistantapi.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadb.workassistantapi.domain.*;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import com.squadb.workassistantapi.web.controller.dto.ReservationRequestDto;
import com.squadb.workassistantapi.web.controller.dto.ReservationResponseTestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.squadb.workassistantapi.domain.MemberType.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    EntityManager em;

    private static String BASE_URL = "/reservation/";
    private static MockHttpSession mockSession = new MockHttpSession();
    private static MemberType MEMBER_TYPE = MemberType.NORMAL;

    @AfterEach
    private void afterEach() {
        mockSession.clearAttributes();
    }

    @Test
    @DisplayName("책 예약 성공")
    public void reserveBook() throws Exception {
        //given
        Member member = createPersistedMember();
        Book book = createPersistedBook();
        addToMemberToSession(member);

        ReservationRequestDto reservationRequestDto = new ReservationRequestDto(book.getId());
        String request = toJson(reservationRequestDto);

        //when
        String response = mockMvc.perform(post(BASE_URL)
                .session(mockSession)
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then
        Reservation reservation = findReservationByResponse(response);
        assertThat(reservation).isNotNull();
        assertThat(reservation.getMember()).isEqualTo(member);
        assertThat(reservation.getBook()).isEqualTo(book);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

    @Test
    @DisplayName("예약 취소 성공")
    public void cancelReservation() throws Exception {
        //given
        Member member = createPersistedMember();
        Book book = createPersistedBook();
        Reservation reservation = createPersistedReservationOf(member, book);
        addToMemberToSession(member);

        //when
        String cancelReservationUrl = BASE_URL + reservation.getId();
        String response = mockMvc.perform(delete(cancelReservationUrl)
                .session(mockSession)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then
        Reservation canceledReservation = findReservationByResponse(response);
        assertThat(canceledReservation).isNotNull();
        assertThat(canceledReservation.getMember()).isEqualTo(member);
        assertThat(canceledReservation.getBook()).isEqualTo(book);
        assertThat(canceledReservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    private void addToMemberToSession(Member member) {
        LoginMember loginMember = new LoginMember(member.getId(), MEMBER_TYPE);
        mockSession.setAttribute(MemberController.LOGIN_ATTRIBUTE_NAME, loginMember);
    }

    private Member createPersistedMember() {
        Member member = Member.createMember("normal@miridih.com", "김일반", "1234", MEMBER_TYPE);
        em.persist(member);
        return member;
    }

    private Book createPersistedBook() {
        Member member = Member.createMember("admin@miridih.com", "관리자", "1234", ADMIN);
        em.persist(member);
        Book book = BookFactory.createBookOutOfStockRegisteredBy(member);
        em.persist(book);
        return book;
    }

    private Reservation createPersistedReservationOf(Member member, Book book) {
        Reservation reservation = Reservation.createReservation(member, book);
        em.persist(reservation);
        return reservation;
    }

    private Reservation findReservationByResponse(String jsonResponse) throws JsonProcessingException {
        ReservationResponseTestDto reservationResponseTestDto = objectMapper.readValue(jsonResponse, ReservationResponseTestDto.class);
        Long reservationId = reservationResponseTestDto.getReservationId();
        return reservationRepository.findById(reservationId).get();
    }

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}