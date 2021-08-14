package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

// TODO: [2021/08/14 양동혁] 대출가능한 도서에 예약 못하도록 막기
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Member member;

    @JoinColumn(name = "book_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Book book;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    public static Reservation createReservation(Member member, Book book) {
        validateCreateReservation(member, book);
        Reservation reservation = new Reservation();
        reservation.member = member;
        reservation.book = book;
        reservation.status = ReservationStatus.WAITING;
        reservation.reservationDate = LocalDateTime.now();
        return reservation;
    }

    private static void validateCreateReservation(Member member, Book book) {
        validateNotNull(member, book);
        if (!book.canReserve()) {
            throw new ReservationException("이미 예약중인 책입니다.");
        }
    }

    private static void validateNotNull(Member member, Book book) {
        if (isNull(member) || isNull(book)) {
            throw new ReservationException("예약 필수 정보가 없습니다.");
        }
    }
}
