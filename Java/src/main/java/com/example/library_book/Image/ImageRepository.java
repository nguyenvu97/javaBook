package com.example.library_book.Image;

import com.example.library_book.Book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface ImageRepository extends JpaRepository<Image,Integer> {
   Optional<Image> findByImg(String fileName);

   Optional<List<Image>> findByBookId(Book book);
}
