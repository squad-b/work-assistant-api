package com.squadb.workassistantapi.reservation.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.book.domain.Isbn;
import com.squadb.workassistantapi.book.domain.StockQuantity;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.member.domain.MemberType;
import com.squadb.workassistantapi.member.dto.LoginMember;
import com.squadb.workassistantapi.member.presentation.MemberController;
import com.squadb.workassistantapi.reservation.domain.Reservation;
import com.squadb.workassistantapi.reservation.domain.ReservationRepository;
import com.squadb.workassistantapi.reservation.domain.ReservationStatus;
import com.squadb.workassistantapi.reservation.domain.ReservationValidator;
import com.squadb.workassistantapi.reservation.dto.ReservationRequestDto;
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
import java.time.LocalDateTime;
import java.util.List;

import static com.squadb.workassistantapi.member.domain.MemberType.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
        mockMvc.perform(post(BASE_URL)
                .session(mockSession)
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk());

        //then
        List<Reservation> reservationList = reservationRepository.findAll();
        assertThat(reservationList.size()).isEqualTo(1);
        Reservation reservation = reservationList.get(0);
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
        mockMvc.perform(delete(cancelReservationUrl)
                .session(mockSession)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        Reservation canceledReservation = reservationRepository.findById(reservation.getId()).get();
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
        Book book = createBookOutOfStockRegisteredBy(member);
        em.persist(book);
        return book;
    }

    private Reservation createPersistedReservationOf(Member member, Book book) {
        Reservation reservation = Reservation.createReservation(member, book, mock(ReservationValidator.class));
        em.persist(reservation);
        return reservation;
    }

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static Book createBookOutOfStockRegisteredBy(Member member) {
        int stockQuantity = 0;
        return Book.builder()
                .isbn(Isbn.valueOf("9780596520687"))
                .title("Spring")
                .stockQuantity(StockQuantity.valueOf(stockQuantity))
                .registrationDate(LocalDateTime.now())
                .registrant(member)
                .build();
    }
}