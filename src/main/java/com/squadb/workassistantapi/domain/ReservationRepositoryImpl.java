package com.squadb.workassistantapi.domain;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.squadb.workassistantapi.web.controller.dto.ReservationSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.squadb.workassistantapi.domain.QBook.book;
import static com.squadb.workassistantapi.domain.QMember.member;
import static com.squadb.workassistantapi.domain.QReservation.reservation;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositorySupport {

    private final JPAQueryFactory query;

    @Override
    public Page<Reservation> findAllReservation(ReservationSearchDto reservationSearchDto, Pageable pageable) {
        QueryResults<Reservation> results = query.selectFrom(reservation)
                .join(reservation.member, member).fetchJoin()
                .join(reservation.book, book).fetchJoin()
                .where(memberNameEq(reservationSearchDto.getMemberName()))
                .where(bookTitleContains(reservationSearchDto.getBookTitle()))
                .where(reservationIdEq(reservationSearchDto.getReservationId()))
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    private BooleanExpression bookTitleContains(String bookTitle) {
        if (bookTitle.isBlank()) {
            return null;
        }
        return book.title.contains(bookTitle);
    }

    private BooleanExpression reservationIdEq(Long reservationId) {
        if (reservationId == null) {
            return null;
        }
        return reservation.id.eq(reservationId);
    }

    private BooleanExpression memberNameEq(String memberName) {
        if (memberName.isBlank()) {
            return null;
        }
        return member.name.eq(memberName);
    }
}
