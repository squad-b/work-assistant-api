package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
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
        testBook = createBook(testMember, Isbn.valueOf("9791157596225"));
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
        testBook.decreaseStock();
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

    @DisplayName("책 여러권 반납 테스트")
    @Test
    public void returnBooksTest() {
        LoginMember loginMember = new LoginMember(testMember.getId(), testMember.getType());
        List<Rental> rentalList = new ArrayList<>();
        List<Book> bookList = new ArrayList<>();
        Map<Long, StockQuantity> stockQuantityBeforeReturn = new HashMap<>();
        List<Isbn> isbns = List.of(Isbn.valueOf("9780596520687"), Isbn.valueOf("9780596520587"), Isbn.valueOf("9780596520487"));
        for (int i=0; i<3; ++i) {
            Book book = createBook(testMember, isbns.get(i));
            bookList.add(book);
            Rental rental = createRental(book);
            rentalList.add(rental);
            stockQuantityBeforeReturn.put(book.getId(), book.getStockQuantity());
        }
        List<Long> rentalIdList = rentalList.stream().mapToLong(Rental::getId).boxed().collect(Collectors.toList());
        assertThatNoException().isThrownBy(() -> rentalService.returnBooks(rentalIdList, loginMember));
        rentalList.forEach(rental -> assertThat(rental.isReturned()).isTrue());
        bookList.forEach(book -> assertThat(book.getStockQuantity()).isEqualTo(stockQuantityBeforeReturn.get(book.getId()).plusOne()));
    }

    private Rental createRental(Book testBook) {
        final Rental mockRental = Rental.createRental(testBook, testMember, false, LocalDateTime.now());
        entityManager.persist(mockRental);
        return mockRental;
    }

    private Book createBook(Member member, Isbn isbn) {
        Book book = Book.builder()
                .isbn(isbn)
                .title("제목")
                .author("작가")
                .description("설명")
                .imageUrl("book.img.url")
                .category(BookCategory.DEVELOP)
                .publisher("출판사")
                .stockQuantity(StockQuantity.valueOf(1))
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