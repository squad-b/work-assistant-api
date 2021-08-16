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
    // TODO: [2021/08/15 양동혁] naming: member -> reserver
    private Member member;

    @JoinColumn(name = "book_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Book book;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    private static final int RETENTION_PERIOD_OF_RENTAL = 3;

    public static Reservation createReservation(Member member, Book book) {
        validateCreateReservation(member, book);
        Reservation reservation = new Reservation();
        reservation.member = member;
        reservation.book = book;
        reservation.status = ReservationStatus.WAITING;
        reservation.reservationDate = LocalDateTime.now();
        return reservation;
    }

    // TODO: [2021/08/16 양동혁] naming: 검증메소드
    private static void validateCreateReservation(Member member, Book book) {
        validateNotNull(member, book);
        if (book.canRental()) {
            throw new ReservationException(ReservationErrorCode.NOT_RESERVABLE, "대여가능한 책은 예약할 수 없습니다.");
        }
    }

    private static void validateNotNull(Object... params) {
        for (Object param : params) {
            validateNotNull(param);
        }
    }

    private static void validateNotNull(Object param) {
        if (isNull(param)) {
            throw new ReservationException(ReservationErrorCode.REQUIRED_RESERVATION);
        }
    }

    public void cancelBy(Member member) {
        validateCancelBy(member);
        status = ReservationStatus.CANCELED;
    }

    private void validateCancelBy(Member member) {
        validateNotNull(member);
        if (!isReservedBy(member)) {
            throw new ReservationException(ReservationErrorCode.NOT_AUTHORIZED);
        }
        if (!isStatusWaiting()) {
            throw new ReservationException(ReservationErrorCode.ILLEGAL_STATUS, "대기 중인 예약만 취소할 수 있습니다.");
        }
    }

    public boolean isReservedBy(Member targetMember) {
        return this.member == targetMember;
    }

    private boolean isStatusWaiting() {
        return status == ReservationStatus.WAITING;
    }

    public boolean revokeReservationExpiringOn(LocalDateTime targetDate) {
        validateNotNull(targetDate);
        if (isExpiringOn(targetDate)) {
            status = ReservationStatus.REVOKED;
            return true;
        }
        return false;
    }

    private boolean isExpiringOn(LocalDateTime targetDate) {
        LocalDateTime expiryDate = getExpiryDate();
        return expiryDate.isBefore(targetDate);
    }

    /**
     * 대여가능날짜를 포함해 대야유보기간이 지난다면 예약이 만료된다.
     * ex) 대어유보기간이 3일이고 대출가능 시간이 1일 15시라면 만료일은 4일 00시 이다.
     */
    private LocalDateTime getExpiryDate() {
        return reservationDate.plusDays(RETENTION_PERIOD_OF_RENTAL)
                .toLocalDate()
                .atStartOfDay();
    }
}
