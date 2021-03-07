package com.squadb.workassistantapi.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column
    private String description;

    @Column
    private String author;

    @Column(nullable = false)
    private int stockQuantity;

    @Column
    private String imageUrl;

    @Column
    private LocalDateTime publishingDate;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column
    private String publisher;

    @Enumerated(EnumType.STRING)
    @Column
    private BookCategory category;

}
