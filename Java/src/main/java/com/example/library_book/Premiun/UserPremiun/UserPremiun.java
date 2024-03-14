package com.example.library_book.Premiun.UserPremiun;

import com.example.library_book.OrderBook.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserPremiun {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private int userId;
    private int premiunId;
    private String premiunSatus;
    private LocalDateTime createTime;
    private double premiunPrice;
    private OrderStatus orderStatus;

}
