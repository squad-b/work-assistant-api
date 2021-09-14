package com.squadb.workassistantapi.rental.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.member.dto.LoginMember;
import com.squadb.workassistantapi.reservation.domain.ReservationFinisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.squadb.workassistantapi.rental.domain.RentalStatus.ON_RENTAL;
import static com.squadb.workassistantapi.rental.domain.RentalStatus.RETURN;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental {

    private static final int NORMAL_RENTAL_DAYS = 14;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @JoinColumn(name = "book_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Book book;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Member member;

    @Getter
    @Column(nullable = false)
    private LocalDateTime startDate;

    @Getter
    @Column
    private LocalDateTime endDate;

    @Column
    @Getter(AccessLevel.PACKAGE)
    private LocalDateTime returnDate;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    private Rental(RentalStatus status, LocalDateTime startDate, LocalDateTime endDate, Member member, Book book) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.member = member;
        this.book = book;
    }

    public static Rental createRental(Book book, Member member, boolean isLongTerm, LocalDateTime rentalStartDate,
                                      RentalValidator rentalValidator, ReservationFinisher reservationFinisher) {
        rentalValidator.validateNotExistsOtherMemberReservation(book, member);
        reservationFinisher.finish(book, member);
        book.decreaseStock();
        final LocalDateTime endDate = isLongTerm ? null : rentalStartDate.plusDays(NORMAL_RENTAL_DAYS);
        return new Rental(ON_RENTAL, rentalStartDate,endDate, member, book);
    }

    public Long getMemberId() {
        return member.getId();
    }

    public String getMemberName() {
        return member.getName();
    }

    public Long getBookId() {
        return book.getId();
    }

    public boolean onRental() {
        return status.onRental();
    }

    public boolean isLongTerm() {
        return endDate == null;
    }

    public String getBookTitle() {
        return book.getTitle();
    }

    public void returnBy(LoginMember loginMember) {
        if (!loginMember.isAdmin() && loginMember.is(member)) {
            throw new NoAuthorizationException("반납할 권한이 없습니다.");
        }
        this.status = RETURN;
        this.book.increaseStock();
        this.returnDate = LocalDateTime.now();
    }

    public void returnBy(Member member, LocalDateTime returnDate) {
        if (!member.isAdmin() && !member.equals(this.member)) {
            throw new NoAuthorizationException("관리자 또는 책의 대여자만 책 반납이 가능합니다.");
        }
        this.status = RETURN;
        this.book.increaseStock();
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return this.status.equals(RETURN);
    }
}
