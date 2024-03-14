package com.example.library_book.Book;

import com.example.library_book.Book.Category.Category;
import com.example.library_book.Dto.BookCategoryDto;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.Image.Image;
import com.example.library_book.Image.ImageRepository;
import com.example.library_book.Image.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.valueOf;

@RestController
@RequestMapping("api/v1/book")
public class BookController {
    @Autowired
    public BookService bookService;
    @Autowired
    public BookRepository bookRepository;
    @Autowired
    private ImageService imageService;

    @PostMapping("category")
    public ResponseEntity<List<Category>> addCategory(@RequestBody List<Category> categories){
        return  ResponseEntity.ok().body(bookService.addCategory(categories));
    }


    @PostMapping("create")
    public ResponseEntity<?> addBook(@RequestParam(required = false,value = "video") MultipartFile video,@RequestParam(required = false,value = "image") List<MultipartFile> image,@RequestParam String book){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Book book1 = objectMapper.readValue(book,Book.class);
            return ResponseEntity.ok().body(bookService.addBook(video,image,book1));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("null");
        }
    }
    @GetMapping("/{videoName}")
    public ResponseEntity<?> getVideo(@PathVariable String videoName) {
        Book video = bookRepository.findByMp4(videoName).orElse(null);
        if (video == null ) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));

        try {
            Resource videoResource = new FileSystemResource(video.getPathMp4());
            long contentLength = videoResource.contentLength();

            InputStreamResource videoStream = new InputStreamResource(videoResource.getInputStream());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(contentLength)
                    .body(videoStream);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/fileSystem/{filename}")// upload web
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String filename) throws IOException {
        byte[] imageData=imageService.uploadFilesImage(filename);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(valueOf("image/png"))
                .body(imageData);
    }
   @GetMapping()
    public ResponseEntity<BookCategoryDto> findByIdBook(@RequestParam int bookId){
        return ResponseEntity.ok().body(bookService.findByBookId(bookId));
   }
    @GetMapping("/categoryNameforBook")
    private ResponseEntity<Map<String,List<BookCategoryDto>>> categoryNameForBook(){
        return ResponseEntity.ok().body(bookService.listBooksByCategory());
    }
}
