package com.example.library_book.OrderBook;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderBook {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String orderNo;
    private String bookName;
    private String categoryName;
    private double bookPrice;
    private String img;
    private int userId;
    private int quantity;
    private double totalMoney;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;
}
