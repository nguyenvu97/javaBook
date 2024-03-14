package com.example.library_book.Dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderBookDto {
    private Integer bookId;
    private String bookName;
    private String categoryName;
    private double bookPrice;
    private String author;
    private List<String> imageName;
    private LocalDateTime publishingyear;
    private String title;
    private String orderNo;
    private int userId;
}
