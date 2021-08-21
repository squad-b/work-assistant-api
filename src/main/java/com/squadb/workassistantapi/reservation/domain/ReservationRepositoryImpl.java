package com.squadb.workassistantapi.reservation.domain;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.squadb.workassistantapi.domain.QReservation;
import com.squadb.workassistantapi.reservation.dto.ReservationSearchAllDto;
import com.squadb.workassistantapi.reservation.dto.ReservationSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.squadb.workassistantapi.domain.QBook.book;
import static com.squadb.workassistantapi.domain.QMember.member;
import static com.squadb.workassistantapi.domain.QReservation.reservation;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositorySupport {

    private final JPAQueryFactory query;

    @Override
    public List<Reservation> findAllByBookIdAndStatus(Long bookId, ReservationStatus status) {
        return query.selectFrom(reservation)
                .join(reservation.member, member).fetchJoin()
                .where(reservation.status.eq(status))
                .fetch();
    }


    @Override
    public Page<Reservation> findAllByMemberIdAndStatus(Long memberId, ReservationStatus status, Pageable pageable) {
        QueryResults<Reservation> results = query.selectFrom(reservation)
                .join(reservation.member, member).fetchJoin()
                .join(reservation.book, book).fetchJoin()
                .where(book.id.eq(memberId))
                .where(reservation.status.eq(status))
                .orderBy(reservation.id.desc())
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    @Override
    public List<Reservation> findAllByMemberIdAndStatus(Long memberId, ReservationStatus status) {
        return query.selectFrom(reservation)
                .join(reservation.member, member).fetchJoin()
                .join(reservation.book, book).fetchJoin()
                .where(book.id.eq(memberId))
                .where(reservation.status.eq(status))
                .fetch();
    }

    @Override
    public Page<Reservation> findAllBySearchAll(ReservationSearchAllDto reservationSearchAllDto, Pageable pageable) {
        QueryResults<Reservation> results = query.selectFrom(reservation)
                .join(reservation.member, member).fetchJoin()
                .join(reservation.book, book).fetchJoin()
                .where(memberNameEq(reservationSearchAllDto.getMemberName()))
                .where(bookTitleContains(reservationSearchAllDto.getBookTitle()))
                .where(reservationIdEq(reservationSearchAllDto.getReservationId()))
                .where(statusEQ(reservationSearchAllDto.getReservationStatus()))
                .orderBy(reservation.id.desc())
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    @Override
    public List<Reservation> findAllWithBookByStatus(ReservationStatus reservationStatus) {
        return query.selectFrom(reservation)
                .join(reservation.book).fetchJoin()
                .where(reservation.status.eq(reservationStatus))
                .fetch();
    }

    @Override
    public Optional<Reservation> findReservationWithMemberBySearch(ReservationSearchDto reservationSearchDto) {
        Long memberId = reservationSearchDto.getMemberId();
        Long bookId = reservationSearchDto.getBookId();
        Reservation reservation = query.selectFrom(QReservation.reservation)
                .join(QReservation.reservation.member, member).fetchJoin()
                .join(QReservation.reservation.book, book)
                .where(member.id.eq(memberId), book.id.eq(bookId))
                .fetchOne();
        return Optional.ofNullable(reservation);

    }

    private BooleanExpression memberNameEq(String memberName) {
        if (memberName.isBlank()) {
            return null;
        }
        return member.name.eq(memberName);
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

    private BooleanExpression statusEQ(ReservationStatus reservationStatus) {
        if (reservationStatus == null) {
            return null;
        }
        return reservation.status.eq(reservationStatus);
    }
}
