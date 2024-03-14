package com.example.library_book.Book;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String bookName;
    private int categoryId;
    private double bookPrice;
    private LocalDateTime create_At;
    private LocalDateTime update_At;
    private String author;
    private String title;
    private LocalDateTime publishingyear;
    private String mp4;
    private String pathMp4;
}
