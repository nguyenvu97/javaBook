package com.example.library_book.Dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookCategoryDto {
    private Integer id;
    private String bookName;
    private String categoryName;
    private double bookPrice;
    private String author;
    private String title;
    private LocalDateTime publishingyear;
    private String mp4;
    private String pathMp4;
    private List<String> imageName;
}
