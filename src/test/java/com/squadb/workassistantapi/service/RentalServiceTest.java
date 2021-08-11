package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import com.squadb.workassistantapi.domain.exceptions.OutOfStockException;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class RentalServiceTest {
    @Autowired EntityManager entityManager;
    @Autowired RentalService rentalService;

    private Member testMember;
    private Book testBook;

    @BeforeEach
    public void setup() {
        testMember = createMember();
        testBook = createBook(testMember);
    }

    @DisplayName("기본 책 대여 테스트")
    @Test
    public void rentBookTest() {
        // when
        final long rentalId = rentalService.rentBook(testBook.getId(), testMember.getId(), false);
        clearPersistenceContext();

        // then
        Rental rental = rentalService.findById(rentalId);
        assertThat(rental.getMemberId()).isEqualTo(testMember.getId());
        assertThat(rental.getBookId()).isEqualTo(testBook.getId());
        assertThat(rental.onRental()).isTrue();
        assertThat(rental.isLongTerm()).isFalse();
        assertThat(rentalId).isGreaterThan(0L);

        testBook = entityManager.find(Book.class, testBook.getId());
        assertThat(testBook.isOutOfStock()).isTrue();
    }

    @DisplayName("책 재고가 없을때는 대여할 수 없다.")
    @Test
    public void outOfStockTest() {
        // given
        testBook.removeStock();
        clearPersistenceContext();

        // then
        Assertions.assertThrows(OutOfStockException.class, () -> {
            rentalService.rentBook(testBook.getId(), testMember.getId(), false);
        });
    }

    @DisplayName("장기 대여시에는 rental 의 endDate 가 null 값이다.")
    @Test
    public void longTermRentalTest() {
        final long rentalId = rentalService.rentBook(testBook.getId(), testMember.getId(), true);
        clearPersistenceContext();

        Rental rental = rentalService.findById(rentalId);
        assertThat(rental.isLongTerm()).isTrue();
    }

    @DisplayName("본인이 빌린 책을 반납하면 책의 상태가 stockQuantity 가 오르고, 해당 Rental 의 status 가 RETURN 으로 바뀐다.")
    @Test
    public void returnBookSuccessTest() {
        // given
        final int stockQuantityBeforeReturn = testBook.getStockQuantity();
        final Rental mockRental = createRental(testBook);

        // when
        final long rentalId = rentalService.updateRental(mockRental.getId(), testMember.getId(), RentalStatus.RETURN);

        // then
        assertThat(rentalId).isEqualTo(mockRental.getId());
        assertThat(testBook.getStockQuantity()).isEqualTo(stockQuantityBeforeReturn);
        assertThat(mockRental.onRental()).isFalse();
    }

    @DisplayName("본인이 빌리지 않은 책을 반납하면 예외가 발생한다.")
    @Test
    public void returnBookFailTest() {
        // given
        final Rental mockRental = createRental(testBook);
        final long notRentalOwnerId = mockRental.getId() + 1;

        // then
        Assertions.assertThrows(NoAuthorizationException.class, () -> rentalService.updateRental(mockRental.getId(), notRentalOwnerId, RentalStatus.RETURN));
    }

    @DisplayName("유저의 책 대여 목록 조회 테스트")
    @Test
    public void memberBookRentalTest() {
        // given
        Rental mockRental = createRental(createBook(testMember));
        rentalService.updateRental(mockRental.getId(), testMember.getId(), RentalStatus.ON_RENTAL);

        Rental mockReturnedRental = createRental(createBook(testMember));
        rentalService.updateRental(mockReturnedRental.getId(), testMember.getId(), RentalStatus.RETURN);

        // when
        List<Rental> rentalList = rentalService.findMemberBookRentals(testMember.getId(), null);
        List<Rental> onRentalList = rentalService.findMemberBookRentals(testMember.getId(), RentalStatus.ON_RENTAL);
        List<Rental> returnedRentalList = rentalService.findMemberBookRentals(testMember.getId(), RentalStatus.RETURN);

        // then
        rentalList.forEach(rental -> {
            assertThat(rental.getMemberId()).isEqualTo(testMember.getId());
            assertThat(rental.getStatus()).isIn(RentalStatus.ON_RENTAL, RentalStatus.RETURN);
        });
        onRentalList.forEach(rental -> {
            assertThat(rental.getMemberId()).isEqualTo(testMember.getId());
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.ON_RENTAL);
        });
        returnedRentalList.forEach(rental -> {
            assertThat(rental.getMemberId()).isEqualTo(testMember.getId());
            assertThat(rental.getStatus()).isEqualTo(RentalStatus.RETURN);
        });
    }

    @DisplayName("책 여러권 반납 테스트")
    @Test
    public void returnBooksTest() {
        LoginMember loginMember = new LoginMember(testMember.getId(), testMember.getType());
        List<Rental> rentalList = new ArrayList<>();
        List<Book> bookList = new ArrayList<>();
        Map<Long, Integer> stockQuantityBeforeReturn = new HashMap<>();
        for (int i=0; i<3; ++i) {
            Book book = createBook(testMember);
            bookList.add(book);
            Rental rental = createRental(book);
            rentalList.add(rental);
            stockQuantityBeforeReturn.put(book.getId(), book.getStockQuantity());
        }
        List<Long> rentalIdList = rentalList.stream().mapToLong(Rental::getId).boxed().collect(Collectors.toList());
        assertThatNoException().isThrownBy(() -> rentalService.returnBooks(rentalIdList, loginMember));
        rentalList.forEach(rental -> assertThat(rental.isReturned()).isTrue());
        bookList.forEach(book -> assertThat(book.getStockQuantity() == stockQuantityBeforeReturn.get(book.getId()) + 1).isTrue());
    }

    private Rental createRental(Book testBook) {
        final Rental mockRental = Rental.createRental(testBook, testMember, false);
        entityManager.persist(mockRental);
        return mockRental;
    }

    private Book createBook(Member member) {
        Book book = Book.builder()
                .isbn(Isbn.valueOf(String.format("%.13f", Math.random()).substring(2)))
                .title("제목")
                .author("작가")
                .description("설명")
                .imageUrl("book.img.url")
                .category(BookCategory.DEVELOP)
                .publisher("출판사")
                .stockQuantity(1)
                .publishingDate(LocalDateTime.now())
                .registrant(member)
                .registrationDate(LocalDateTime.now())
                .build();
        entityManager.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = Member.createMember("test@naver.com", "피플팀", "12345", MemberType.ADMIN);
        entityManager.persist(member);
        return member;
    }

    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }

}