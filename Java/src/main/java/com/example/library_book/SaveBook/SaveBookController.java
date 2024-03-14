package com.example.library_book.SaveBook;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/save")
public class SaveBookController {
    @Autowired
    public SaveBookService saveBookService;
    @PostMapping("create")
    public ResponseEntity<?>saveBook(@RequestParam int bookId, @RequestHeader("Authorization") String token,@RequestParam String loginStatus){
        return ResponseEntity.ok().body(saveBookService.addSaveBook(bookId,token,loginStatus));
    }
}
