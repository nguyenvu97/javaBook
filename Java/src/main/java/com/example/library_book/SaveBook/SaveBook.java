package com.example.library_book.SaveBook;

import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SaveBook {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private int bookId;
    private int userId;
    private String bookName;
    private String bookCategory;
    private String mp4;
    private String pathMp4;
    private String imageName;
}
