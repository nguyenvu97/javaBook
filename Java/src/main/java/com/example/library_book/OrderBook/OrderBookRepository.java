package com.example.library_book.OrderBook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderBookRepository extends JpaRepository<OrderBook,Integer> {
}
