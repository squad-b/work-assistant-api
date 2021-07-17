package com.squadb.workassistantapi.domain;

import static com.squadb.workassistantapi.domain.RentalStatus.*;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private LocalDateTime returnDate;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    public static Rental createRental(Book book, Member member, boolean isLongTerm) {
        book.removeStock();

        Rental rental = new Rental();
        rental.status = ON_RENTAL;
        rental.startDate = LocalDateTime.now();
        rental.endDate = isLongTerm ? null : rental.startDate.plusDays(NORMAL_RENTAL_DAYS);
        rental.member = member;
        rental.book = book;

        return rental;
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

    public void updateRental(Long memberId, RentalStatus updateStatus) {
        if (member.isNotEqualId(memberId)) { throw new NoAuthorizationException(String.format("반납 권한이 없습니다. rentalId: %d, memberId: %d", id, memberId)); }
        if (updateStatus == RETURN) {
            status = RETURN;
            book.increaseStock();
            returnDate = LocalDateTime.now();
        }
    }

    public void returnBy(LoginMember loginMember) {
        if (!loginMember.isAdmin() && loginMember.is(member)) {
            throw new NoAuthorizationException("반납할 권한이 없습니다.");
        }
        this.status = RETURN;
        this.book.increaseStock();
        this.returnDate = LocalDateTime.now();
    }

    public boolean isReturned() {
        return this.status.equals(RETURN);
    }
}
