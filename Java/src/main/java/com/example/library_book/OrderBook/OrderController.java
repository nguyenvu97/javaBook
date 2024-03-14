package com.example.library_book.OrderBook;

import com.example.library_book.Dto.OrderBookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orderBook")
public class OrderController {
    @Autowired
    public OrderBookService orderBookService;

    @PostMapping("create")
    public ResponseEntity<OrderBookDto>createBook(@RequestHeader("Authorization") String token,
                                                  @RequestParam String loginStatus,
                                                  @RequestParam int bookId,
                                                  @RequestParam int quantity){
        return ResponseEntity.ok().body(orderBookService.createOrderBook(bookId,token,loginStatus,quantity));
    }
}
