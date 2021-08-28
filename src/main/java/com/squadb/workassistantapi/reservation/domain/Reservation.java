package com.squadb.workassistantapi.reservation.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.book.domain.BookCategory;
import com.squadb.workassistantapi.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    private static final int RETENTION_DAYS_OF_RENTAL = 3;

    public static final int MAX_COUNT_PER_MEMBER = 3;

    public static final int MAX_COUNT_PER_BOOK = 5;

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


    @Builder
    private Reservation(Member member, Book book, ReservationStatus status, LocalDateTime reservationDate) {
        this.member = member;
        this.book = book;
        this.status = status;
        this.reservationDate = reservationDate;
    }

    public static Reservation createReservation(Member member, Book book, ReservationValidator reservationValidator) {
        validateNotNull(member, book);
        validateBookOutOfStock(book);
        reservationValidator.validateCanReserve(member, book);
        return Reservation.builder()
                .member(member)
                .book(book)
                .status(ReservationStatus.WAITING)
                .reservationDate(LocalDateTime.now())
                .build();
    }

    private static void validateBookOutOfStock(Book book) {
        if (!book.isOutOfStock()) {
            throw new ReservationException(ReservationErrorCode.NOT_RESERVABLE, "대여가능한 책은 예약할 수 없습니다.");
        }
    }

    public void cancelBy(Member member) {
        validateNotNull(member);
        validateReservedBy(member);
        validateIsStatusWaiting();
        status = ReservationStatus.CANCELED;
    }

    private void validateReservedBy(Member member) {
        if (!isReservedBy(member)) {
            throw new ReservationException(ReservationErrorCode.NOT_AUTHORIZED);
        }
    }

    public boolean isReservedBy(Member targetMember) {
        return this.member == targetMember;
    }

    public boolean canRentable() {
        return !book.isOutOfStock();
    }

    private void validateIsStatusWaiting() {
        if (!status.isWaiting()) {
            throw new ReservationException(ReservationErrorCode.ILLEGAL_STATUS);
        }
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
        LocalDateTime expiryDate = findExpiryDate();
        return expiryDate.isBefore(targetDate);
    }

    /**
     * 대여가능날짜를 포함해 대여유보기간이 지난다면 예약이 만료된다.
     * ex) 대어유보기간이 3일이고 대출가능 시간이 1일 15시라면 만료일은 4일 00시 이다.
     */
    private LocalDateTime findExpiryDate() {
        return reservationDate.plusDays(RETENTION_DAYS_OF_RENTAL)
                .toLocalDate()
                .atStartOfDay();
    }

    public void finishedBy(Member member) {
        validateNotNull(member);
        validateReservedBy(member);
        status = ReservationStatus.FINISHED;
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

    public String getMemberName() {
        return member.getName();
    }

    public BookCategory getBookCategory() {
        return book.getCategory();
    }
}
