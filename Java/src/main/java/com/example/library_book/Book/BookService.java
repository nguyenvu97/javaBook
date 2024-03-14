package com.example.library_book.Book;

import com.example.library_book.Book.Category.Category;
import com.example.library_book.Book.Category.CategoryRepository;
import com.example.library_book.Dto.BookCategoryDto;
import com.example.library_book.Dto.MemberData;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.Image.Image;
import com.example.library_book.Image.ImageRepository;
import com.example.library_book.Image.ImageService;
import com.example.library_book.JwtDecode.JwtDecode;
import com.example.library_book.Oauth2.LoginStatus;
import com.example.library_book.Video.VideoService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class BookService {
    @Autowired
    public BookRepository bookRepository;
    @Value("${uploading.videoSaveFolder}")
    private String FOLDER_PATH;
    @Autowired
    private VideoService videoService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private JwtDecode jwtDecode;

    public List<Category> addCategory(List<Category>categories){
        List<Category>categories1 = new ArrayList<>();
        for (Category category :categories){
            Category category1 = categoryRepository.save(Category
                    .builder()
                            .categoryName(category.getCategoryName())
                    .build());
            categories1.add(category1);
        }
        return categoryRepository.saveAll(categories1);

    }
    public Book addBook(MultipartFile videoFlies, List<MultipartFile> image,Book book) throws IOException {
        if (videoFlies.isEmpty() && image.isEmpty() ){
            throw new RuntimeException("not found");
        }
        String randomImageName = LocalDateTime.now().toString();
        String pathImg = FOLDER_PATH + "/" + randomImageName;
        String pathAudio = FOLDER_PATH + "/" + randomImageName+".mp4";
        videoFlies.transferTo(new File(pathImg));
        Book  book1 =  bookRepository.save(Book
                .builder()

                        .bookName(book.getBookName())
                        .bookPrice(book.getBookPrice())
                        .title(book.getTitle())
                        .create_At(LocalDateTime.now())
                        .mp4(randomImageName)
                        .pathMp4(pathAudio)
                        .author(book.getAuthor())
                        .categoryId(book.getCategoryId())
                        .create_At(LocalDateTime.now())
                        .update_At(LocalDateTime.now())
                        .publishingyear(book.getPublishingyear())
                .build());
        videoService.AddVideo(videoFlies,pathAudio,pathImg);
        imageService.addImages(image,book1);
        return book1;
    }

    public BookCategoryDto convetBook(Book book,List<Image>images , Category category){
        BookCategoryDto bookCategoryDto = new BookCategoryDto();
        bookCategoryDto.setId(book.getId());
        bookCategoryDto.setBookName(book.getBookName());
        bookCategoryDto.setBookPrice(book.getBookPrice());
        bookCategoryDto.setAuthor(book.getAuthor());
        bookCategoryDto.setTitle(book.getTitle());
        bookCategoryDto.setMp4(book.getMp4());
        bookCategoryDto.setPathMp4(book.getPathMp4());
        bookCategoryDto.setCategoryName(category.getCategoryName());
        bookCategoryDto.setImageName(images.stream().map(Image::getImg).collect(Collectors.toList()));
        return bookCategoryDto;
    }
    public BookCategoryDto findByBookId(int bookId) {
        if (bookId <= 0) {
            throw new FindByNoId("Invalid book ID: " + bookId);
        }

        return bookRepository.findById(bookId)
                .map(book -> {
                    List<Image> image =  imageRepository.findByBookId(book).orElseThrow(null);
                    System.out.println(image);
                    Category category = categoryRepository.findById(book.getCategoryId())
                            .orElseThrow(() -> new FindByNoId("Category not found with ID: " + book.getCategoryId()));

                    BookCategoryDto bookCategoryDto = convetBook(book,image,category);

                    return bookCategoryDto;
                })
                .orElseThrow(() -> new FindByNoId("Book not found with ID: " + bookId));
    }
    public Map<String, List<BookCategoryDto>> listBooksByCategory() {
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(book -> {
                    List<Image> images = imageRepository.findByBookId(book).orElseThrow(null);
                    Category category = categoryRepository.findById(book.getCategoryId())
                            .orElseThrow(() -> new FindByNoId("Category not found with ID: " + book.getCategoryId()));
                    BookCategoryDto bookCategoryDto = convetBook(book,images,category);
                    return bookCategoryDto;
                })
                .collect(Collectors.groupingBy(BookCategoryDto::getCategoryName));
    }



}
