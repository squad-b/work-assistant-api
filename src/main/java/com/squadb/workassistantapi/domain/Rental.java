package com.squadb.workassistantapi.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.squadb.workassistantapi.domain.RentalStatus.ON_RENTAL;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental {
    private static final long NORMAL_RENTAL_DAYS = 14;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    public String getMemberName() {
        return member.getName();
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

    public String getBookTitle() {
        return book.getTitle();
    }
}
