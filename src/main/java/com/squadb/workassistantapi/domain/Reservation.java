package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

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
        if (book.canRental()) {
            throw new ReservationException(ReservationErrorCode.NOT_RESERVABLE, "대여가능한 책은 예약할 수 없습니다.");
        }
    }

    private static void validateNotNull(Member member, Book book) {
        if (isNull(member) || isNull(book)) {
            throw new ReservationException(ReservationErrorCode.REQUIRED_RESERVATION);
        }
    }

    public boolean isReservedBy(Member targetMember) {
        return this.member == targetMember;
    }
}
