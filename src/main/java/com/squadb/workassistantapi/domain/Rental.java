package com.squadb.workassistantapi.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.squadb.workassistantapi.domain.RentalStatus.ON_RENTAL;

@Entity
public class Rental {
    private static final long NORMAL_RENTAL_DAYS = 14;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "book_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Book book;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private LocalDateTime returnDate;

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

    public long getMemberId() {
        return member.getId();
    }

    public long getBookId() {
        return book.getId();
    }

    public boolean onRental() {
        return status.onRental();
    }

    public boolean isLongTerm() {
        return endDate == null;
    }
}
