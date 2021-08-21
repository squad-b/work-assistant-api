package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberType;
import com.squadb.workassistantapi.service.exception.KeyDuplicationException;
import com.squadb.workassistantapi.web.controller.dto.BookRegisterDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired private BookService bookService;
    @Autowired private EntityManager entityManager;
    @Autowired private PasswordEncryptorImpl passwordEncryptor;

    @DisplayName("책 등록시 isbn 이 중복되면 예외가 발생한다.")
    @Test
    public void isbnDuplicationTest() {
        System.out.println(passwordEncryptor.encrypt("test1234"));
        //given
        final Member 등록자 = 멤버가_등록되어_있다();
        final String isbn = "0512520689";
        책이_등록_되어_있다(isbn, 등록자.getId());

        //when then
        assertThatThrownBy(() -> 책을_등록한다(isbn, 등록자.getId()))
                .isInstanceOf(KeyDuplicationException.class);
    }

    private void 책이_등록_되어_있다(String isbn, Long memberId) {
        책을_등록한다(isbn, memberId);
    }

    private Long 책을_등록한다(String isbn, Long memberId) {
        final BookRegisterDto bookRegisterDto = new BookRegisterDto();
        bookRegisterDto.setIsbn(isbn);
        bookRegisterDto.setTitle("Test");
        bookRegisterDto.setStockQuantity(1);
        return bookService.register(bookRegisterDto, memberId);
    }

    private Member 멤버가_등록되어_있다() {
        final Member member = Member.createMember("admin@test.com", "피플팀", "1234", MemberType.ADMIN);
        entityManager.persist(member);
        entityManager.flush();
        return member;
    }

}
